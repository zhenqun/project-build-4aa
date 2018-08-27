(function (angular) {
    angular.module('mgnt')
        .controller('OpenRoleController',
            function ($scope, $rootScope,$q, $http, toastrService, _) {
                'ngInject';
                $scope.isSubmitting = false;
                $scope.isRoleLoading = false;
                $scope.isRoleAddLoading = false;
                $scope.clientList = [];
                $scope.clientData = [];
                $scope.params = [];
                this.$onInit = function () {
                    $scope.assistManage();
                };

                $scope.assistManage = function(){
                        //查询该安全员能授权给辅助安全员的应用系统
                        $http.post('/assistSecurity/getAssistUserManage').then(function(data){
                            var data = data.data;
                            if(data.length){
                                $scope.clientList = data;
                                return $http.post('/manage/business/assistRoleQuery');
                            }
                            return $q.reject("管理范围不存在");
                        }).then(function (data) {
                            $scope.isRoleLoading = false;
                            if (data.data) {
                                $scope.clientData = data.data;
                                var allClient = [];
                                var newClients = [];
                                $scope.clientData.forEach(function (item) {
                                    allClient.push({
                                        clientId: item.clientId,
                                        clientName: item.clientName
                                    });
                                    newClients.push(item.clientId);
                                });
                                newClients = _.uniq(newClients);
                                var clientRoleList = newClients
                                    .map(function(clientId) {
                                        return allClient.find(function(x) { return x.clientId === clientId; });
                                    })
                                    .map(function(x) {
                                        var roles = $scope.clientData.filter(function(role) {
                                            return role.clientId === x.clientId;
                                        });
                                        return {
                                            clientId: x.clientId,
                                            clientName: x.clientName,
                                            roles: roles
                                        };
                                    });
                                $scope.clientList.forEach(function(x){
                                   var client = clientRoleList.find(function(client){
                                        return x.clientId === client.clientId;
                                    });
                                    if(client!=null){
                                        x.roles = client.roles;
                                    }else {
                                        x.roles = [];
                                    }
                                });
                                return $http.post('/manage/business/assistRoleAddedQuery');
                            }
                            return $q.reject("角色不存在");
                        }).then(function(data){
                            $scope.isRoleAddLoading = false;
                            var addedRoles = data.data;
                            if (addedRoles) {
                                $scope.clientList.forEach(function (client) {
                                    client.roles
                                        .filter(function (role) {
                                            return addedRoles.find(function(x) { return x.roleId === role.roleId });
                                        })
                                        .forEach(function(role) {
                                            role.chk = true;
                                        });
                                });
                            }
                        }).catch(function(error){
                            $scope.isRoleLoading = false;
                            toastrService.error(error);
                        });
                };

                //保存角色包配置
                $scope.save = function () {
                    var selectRole = [];
                    var params = $scope.clientList.reduce(function(prevVal, client) {
                        return prevVal.concat(client.roles.filter(function (role) {
                                return role.chk;
                            }).map(function(x){
                                return {
                                    roleId: x.roleId,
                                    clientId: client.clientId
                                };
                            })
                        );
                    }, []);
                        $scope.isSubmitting = true;
                        $http.post('/manage/business/assistRoleMod', params).then(function(data){
                            var data = data.data;
                            if(data.flag === 'success'){
                                toastrService.success('保存成功');
                                setTimeout(function () {
                                    window.location = '/manage/role-packs';
                                }, 2000);
                            }
                            else{
                                $scope.isSubmitting = false;
                                toastrService.error(data.message);
                            }

                        },function(){
                            $scope.isSubmitting = false;
                            toastrService.error('请求失败');
                        });
                    }
            }
    );

})(angular);

