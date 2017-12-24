var app = angular.module('archive', ['ngDialog']);

app.directive('fileModel', [ '$parse', function($parse) {
    return {
        restrict : 'A',
        link : function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function() {
                scope.$apply(function() {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
} ]);

app.service('ArchiveService', [ '$http', '$rootScope', function($http, $rootScope) {
    this.search = function(name, date) {
        $http.get("http://localhost:9000/archive/documents", {
            params : {
                person : name,
                date : date
            }
        }).success(function(response) {
            $rootScope.metadataList = response;
        }).error(function() {
        });
    }
}]);

app.service('WordsFilterService', [ '$http', '$rootScope', function($http, $rootScope) {
    this.startFilterProcess = function(fileName, user, url) {
        $http.get(url, {
            params : {
                fileName : fileName,
                user : user
            }
        }).success(function(response) {
            $rootScope.page = response;
        }).error(function() {
        });
    }
    this.filterOut = function(fileName, pageNumber, word, url) {
        $http.get(url, {
            params : {
                fileName : fileName,
                pageNumber : pageNumber,
                word : word
            }
        }).success(function(response) {
            $rootScope.page = response;
        }).error(function() {
        });
    }
    this.nextPage = function(fileName, pageNumber, url) {
            $http.get(url, {
                params : {
                    fileName : fileName,
                    pageNumber : pageNumber
                }
            }).success(function(response) {
                $rootScope.page = response;
            }).error(function() {
            });
        }
    this.finishFiltering = function(url) {
            $http.get(url, {
                responseType:'arraybuffer'
            }).success(function(response) {
                $rootScope.generatedPDF = response;
                console.log('generated binary data is ' + response);
                var blob = new Blob([response], { type: 'application/pdf' });
                console.log('generated data is ' + blob);
                var downloadLink = angular.element('<a></a>');
                downloadLink.attr('href',window.URL.createObjectURL(blob));
                downloadLink.attr('download', 'generated.pdf');
                			downloadLink[0].click();
            }).error(function() {
            });
        }

}]);

app.service('fileUpload', ['$http','ArchiveService', function($http, ArchiveService) {
    this.uploadFileToUrl = function(uploadUrl, file, name, date) {
        var fd = new FormData();
        fd.append('file', file);
        fd.append('person', name);
        fd.append('date', date);
        $http.post(uploadUrl, fd, {
            transformRequest : angular.identity,
            headers : {
                'Content-Type' : undefined
            }
        }).success(function() {
            ArchiveService.search(null, null);
        }).error(function() {
        });
    }
} ]);

app.controller('UploadCtrl', [ '$scope', 'fileUpload',
    function($scope, fileUpload) {
        $scope.uploadFile = function() {
            var file = $scope.myFile;
            var name = $scope.name;
            var date = $scope.date;
            console.log('file is ' + JSON.stringify(file));
            var uploadUrl = "/archive/upload";
            fileUpload.uploadFileToUrl(uploadUrl, file, name, date);
        };
    } ]);

app.controller('ArchiveCtrl', function($scope, $http) {
    $scope.search = function(name, date) {
        $http.get("http://localhost:9000/archive/documents", {
            params : {
                person : name,
                date : date
            }
        }).success(function(response) {
            $scope.metadataList = response;
        });
    };
});

app.controller('FilterCtrl', [ '$scope', '$location', 'WordsFilterService', 'ngDialog',
    function($scope, $location, WordsFilterService, ngDialog) {
        $scope.startFiltering = function() {
            var fileName = $scope.metadata.uuid;
            var user = $scope.metadata.personName;
            var url = "/words/startFiltering";
            WordsFilterService.startFilterProcess(fileName, user, url);
            //$location.path('word-filter.html');
            ngDialog.open({template: 'word-filter.html'});
        };
        $scope.filterOut = function() {
            var url = "/words/filterOut";
            var fileName = $scope.page.fileUUID;
            var pageNumber = $scope.page.pageNumber;
            var word = $scope.word;
            WordsFilterService.filterOut(fileName, pageNumber, word, url);
            $scope.$apply();
        };
        $scope.nextPage = function() {
                    var url = "/words/nextPage";
                    var fileName = $scope.page.fileUUID;
                    var pageNumber = $scope.page.pageNumber;
                    WordsFilterService.nextPage(fileName, pageNumber, url);
                    $scope.$apply();
                };
        $scope.finishFiltering = function() {
                    var url = "/words/finishFiltering";
                    WordsFilterService.finishFiltering(url);
        };

    } ]);

app.run(function($rootScope, $http) {
    $http.get("http://localhost:9000/archive/documents").success(
        function(response) {
            $rootScope.metadataList = response;
        });
});

function sortByLabel(claims) {
    claims.sort(function(a, b) {
        var labelA = a.label.toLowerCase(), labelB = b.label.toLowerCase();
        if (labelA < labelB) // sort string ascending
            return -1;
        if (labelA > labelB)
            return 1;
        return 0; // default return value (no sorting)
    });
}