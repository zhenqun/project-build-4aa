(function(angular) {
    angular.module('mgnt')
        .controller('AssistorReauthorizeController', function($scope, $uibModalInstance, toastrService, assistManageService, assistors, FLAGS) {
            'ngInject';

            $scope.isClientLoading = false;
            $scope.isLoading = false;

            var clientList = [];
            $scope.assistors = [];
            $scope.selectedAssistor = null;

            this.$onInit = function() {
                $scope.loadClients()
                    .then(function(clients) {
                        if (clients != null) {
                            assistors.forEach(function(x) {
                                x.clients = angular.copy(clients);
                            });
                            $scope.selectAssistor(assistors[0]);
                        }
                    });

                $scope.assistors = assistors;
            };

            $scope.loadClients = function() {
                $scope.isClientLoading = true;

                return assistManageService.queryClients()
                    .then(function(resp) {
                        $scope.isClientLoading = false;
                        var data = resp.data;
                        clientList = data || [];

                        return clientList;
                    })
                    .catch(function(error) {
                        $scope.isClientLoading = false;
                        toastrService.error(error);

                        return null;
                    });
            };

            $scope.selectAssistor = function(assistor) {
                var oldAssistor = $scope.selectedAssistor;
                if (oldAssistor && oldAssistor.isLoading) {
                    return;
                }
                $scope.selectedAssistor = assistor;

                assistor.isLoading = true;
                assistor.clients = angular.copy(clientList);
                assistManageService.getAssistorClients(assistor.fzuserId)
                    .then(function(resp) {
                        assistor.isLoading = false;

                        var clients = resp.data || [];
                        if (clients) {
                            clients.forEach(function(x) {
                                 var client = assistor.clients.find(function(client) { return client.clientId === x.clientId; });
                                 if (client) {
                                     client._selected = true;
                                     client.manageId = x.manageId;
                                 }
                            });
                        }
                    })
                    .catch(function(error) {
                        assistor.isLoading = false;
                        toastrService.error(error);
                    });
            };

            $scope.saveAuthorization = function() {
                var selectManageError = false;
                var assistor = $scope.selectedAssistor;
                if (assistor == null) {
                    toastrService.warning('请选择辅助安全员');
                    return;
                }
                var params = {
                    fzuserId: assistor.fzuserId,
                    clients: assistor.clients
                        .filter(function(x) {
                            if(x._selected &&(!x.manageId)){
                                selectManageError = true;
                            }
                            return x._selected && x.manageId;
                        })
                        .map(function(x) {
                            var client = {
                                clientId: x.clientId,
                                clientName: x.clientName,
                                createManageId: x.orgId,
                                manageId: x.manageId
                            };
                            var orgInfo = x.manageName;
                            if (orgInfo) {
                                client.manageName = orgInfo.name;
                                var extData = orgInfo.extData;
                                if (extData) {
                                    client.manageCode = extData.code;
                                }
                            }
                            return client;
                        })
                };

                if (params.clients.length === 0) {
                    toastrService.warning('辅助安全员应至少拥有一个应用系统的管理权限');
                    return;
                }

                if(selectManageError){
                    toastrService.warning('请为选中的每个应用系统至少选择一个管理范围');
                    return;
                }

                var errorClient = params.clients.find(function(client) {
                    return (!client.manageId) || (!client.manageName) || (!client.manageCode);
                });
                if (errorClient != null) {
                    toastrService.warning('请为 ' + assistor.relName + ' 分配的 ' + errorClient.clientName + ' 重新选择党组织');
                    return;
                }

                $scope.isLoading = true;
                assistManageService.saveAssistorClients(params)
                    .then(function(resp) {
                        $scope.isLoading = false;

                        var result = resp.data;
                        if (result.flag === FLAGS.Fail) {
                            toastrService.error(result.message);
                        }
                        else {
                            toastrService.success('操作成功');
                            if ($scope.assistors.length === 1) {
                                $uibModalInstance.close();
                            }
                            $scope.selectAssistor(assistor);
                        }
                    })
                    .catch(function(error) {
                        toastrService.error(error);
                        $scope.isLoading = false;
                    });
            };
        });
})(angular);
