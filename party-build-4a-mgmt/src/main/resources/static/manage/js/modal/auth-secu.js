(function (angular) {

    angular.module('mgnt')
        .directive('orgVerify', function ($http, $q) {
            return {
                restrict: 'A',
                require: '^ngModel',
                link: function (scope, attr, ele, ngModel) {
                    // var chk = scope.client.chk;
                    var clientId = ele.clientid;
                    // ngModel.$formatter.push(function(val){
                    //     return val;f
                    // })
                    // ngModel.$render = function(){
                    //     scope.orgId = ngModel.$viewValue;
                    // }
                    ngModel.$asyncValidators.errOrg = function (mval, vval) {
                        if (mval != '') {
                            return $http.post('/manage/business/checkRole', {
                                roleManageId: mval,
                                clientId: clientId
                            })
                                .then(function (result) {
                                    return result.data;
                                    if (result.data) {
                                        return true;
                                    }
                                    else {
                                        $q.reject('invalid orgId')
                                    }
                                })
                        }
                        return $q.reject('invalid orgId')
                    };
                    // ngModel.$validators.nullOrg = function (mval, vval) {
                    //     if (chk) {
                    //         return mval != ''
                    //     }
                    //     else {
                    //         return true
                    //     }
                    // }
                }
            }
        })
        .controller('AuthController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            'toastrService',
            '$http',
            '$uibModalInstance',
            'authUsers',
            '_',
            function ($scope, $window, $timeout, $uibModal, toastrService, $http, $uibModalInstance, authUsers, _) {
                'ngInject';
                this.$onInit = function () {
                    $scope.businessUserIds = authUsers.businessUserIds;
                    var temp = authUsers.clientList;               
                    $scope.businessUserIds = authUsers.businessUserIds;
                    var temp = authUsers.clientList;
                    $scope.origClientList = temp;
                    $scope.userClients = handleClientData(temp);
                    $scope.choosedUsers = authUsers.businessUsers;
                    $scope.selectUser($scope.choosedUsers[0]);
                           
                 
                }
                var businessUserId = '';
                $scope.display = function (client) {
                    $scope.clientList.forEach(function (x) { x.chk = false });
                    client.chk = true;
                }

                $scope.selectChildren = function (client) {
                    client.roles.forEach(function (item) {
                        item.chk = client.chk;
                    })
                }

                $scope.authParmas = [];
                $scope.orgId = '';
                $scope.orgInfo = {
                    manageOrgName: '',
                    manageOrgCode: ''
                };
                $scope.clientIds = [];
                $scope.childrenRoles = [];

                $scope.$watch('clientList', function (newval, oldval) {
                    if (newval == oldval) {
                        return;
                    }
                    $scope.clientList.filter(function (x) { return x.chk }).forEach(function (item) {
                        $scope.childrenRoles.push(item.roles);
                    })
                }, true);

                //change the rolelist when select a user
                $scope.selectUser = function (user) {
                    //storage the "active" state
                    $scope.selectedUserName = user.businessUserName;
                    $scope.selectedUserId = user.businessUserId;
                   
                }


                //transform data to display in "table"
                var handleClientData = function (data) {
                    var temp = [];
                    data.forEach(function (x) {
                        if (x.roles) {
                            x.roles.forEach(function (item, idx) {
                                var obj = angular.merge({}, x, item);
                                obj.clientName = idx == 0 ? x.clientName : "";
                                obj.rowspan = idx == 0 ? x.roles.length.toString() : null;
                                temp.push(obj);
                            });
                        }
                    });
                    return temp;
                }



                //授权请求
                $scope.auth = function () {
                    var choosedRoles = $scope.userClients.filter(function (x) { return x.chk });
                    if (choosedRoles.length == 0) {
                        toastrService.warning('请选择角色');
                    }
                    $scope.authForm.$setSubmitted();



                    // 过滤出全部的应用
                    var clientList = _.uniq(choosedRoles, 'clientId').map(function (x) { return { clientId: x.clientId } });
                    clientList.forEach(function (x) {
                        x.roles = [];
                        choosedRoles.filter(function (item) { return item.clientId == x.clientId })
                            .forEach(function (item) {
                                x.roles.push({
                                    roleId: item.roleId,
                                    manageId: item.manageId,
                                    manageName: item.manageName.name
                                })
                            })
                    })
                   
                    $http.post('/manage/business/grantBusinessUser', clientList)
                        .then(function (data) {
                            var data = data.data;
                            if (data) {
                                toastrService.success('操作成功');
                                $scope.$close();
                            }
                        })
                }
            }])
})(angular);