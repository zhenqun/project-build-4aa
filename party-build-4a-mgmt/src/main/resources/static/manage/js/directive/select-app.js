(function (angular) {
    angular.module('mgnt')
        .component('selectApp', {
            restrict: 'AE',
            templateUrl: 'selectApp.html',
            // require: 'ngModel',
            controller: function ($rootScope, $scope, $http, $timeout) {
                'ngInject';
                // $scope.selectedId = $scope.data[0].clientId;     
                var that = this;

                that.selectClient = {};
                $rootScope.clients.isLoading = true;
                $http.get('/manage/business/clientQuery')
                    .then(function (data) {
                        var data = data.data;
                        if (data) {
                            $rootScope.allAppRoles = data;
                            that.appList = data.map(function (x) {
                                return _.pick(x, 'clientId', 'clientName', 'classificationUrl');
                            });
                            $rootScope.appList = that.appList;
                            $rootScope.selectClient = $rootScope.allAppRoles[0];
                            if (!getUrlParam('clientId') || !data.find(function (x) { return x.clientId == getUrlParam('clientId') })) {
                                that.select(that.appList[0]);
                            }
                            $rootScope.clients.isLoading = false;
                            $timeout(function () {
                                $rootScope.selectClient = $rootScope.allAppRoles.find(function (x) { return x.clientId == getUrlParam('clientId') });
                                that.selectClient = $rootScope.selectClient;
                            });
                            $rootScope.$broadcast('clientsLoaded');
                        }
                        else {
                            toastrService.error();
                        }
                    });


                var getUrlParam = function (name) {
                    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                    var r = window.location.search.substr(1).match(reg);
                    if (r != null) {
                        return unescape(r[2]);
                    }
                    else {
                        return null;
                    }
                };

                that.select = function (item) {
                    var client = $rootScope.allAppRoles.find(function (x) { return x.clientId == item.clientId })
                    $rootScope.selectClient = client;
                    that.selectClient = client;
                    window.history.replaceState({}, 0, window.location.pathname + '?clientId=' + client.clientId);
                };
            }
        });
}(angular));
