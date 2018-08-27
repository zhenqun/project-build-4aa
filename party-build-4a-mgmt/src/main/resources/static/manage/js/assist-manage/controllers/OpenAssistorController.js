(function(angular) {
    angular.module('mgnt')
        .controller('OpenAssistorController', function($scope, $uibModalInstance, assistManageService, toastrService, FLAGS) {
            'ngInject';

            $scope.isClientLoading = false;
            $scope.isSubmitting = false;

            $scope.assistor = {
                relName: '',
                idCard: '',
                telephone: '',
                remark: ''
            };
            $scope.clientList = [];

            this.$onInit = function() {
                $scope.loadClients();
            };

            $scope.loadClients = function() {
                $scope.isClientLoading = true;

                assistManageService.queryClients()
                    .then(function(resp) {
                        $scope.isClientLoading = false;
                        var data = resp.data;
                        $scope.clientList = data || [];
                    })
                    .catch(function(error) {
                        $scope.isClientLoading = false;
                        toastrService.error(error);
                    });
            };

            $scope.add = function() {
                var addForm = $scope.addForm;
                if ($scope.isSubmitting) {
                    return;
                }
                if (addForm.$invalid) {
                    addForm.$setSubmitted();
                    return;
                }

                var assistor = angular.extend({}, $scope.assistor);
                var selectedClients = $scope.clientList.filter(function(x) { return x._selected && x.manageId; });
                if (selectedClients.length === 0) {
                    toastrService.warning('至少为辅助安全员选择一个应用');
                    return;
                }

                assistor.clients = selectedClients
                    .map(function(x) {
                        var client = {
                            clientId: x.clientId,
                            manageId: x.manageId,
                            createManageId: x.orgId
                        };
                        var orgInfo = x.manageName;
                        if (orgInfo) {
                            client.manageName = orgInfo.name;
                            var extData = orgInfo.extData;
                            if (extData) {
                                client.manageCode = extData.code;
                                client.ouName = extData.ouName;
                            }
                        }
                        return client;
                    });
                var hasNotOrg = assistor.clients.some(function(x) {
                    return (!x.manageId) || (!x.manageName) || (!x.manageCode) || (!x.ouName);
                });
                if (hasNotOrg) {
                    toastrService.warning('请重新选择组织');
                    return;
                }

                $scope.isSubmitting = true;
                assistManageService.add(assistor)
                    .then(function(resp) {
                        $scope.isSubmitting = false;

                        var result = resp.data;
                        if (result.flag === FLAGS.Success) {
                            toastrService.success('添加辅助安全员成功');
                            $uibModalInstance.close();
                        }
                        else {
                            toastrService.error(result.message);
                        }
                    })
                    .catch(function(error) {
                        toastrService.error(error);
                        $scope.isSubmitting = false;
                    });
            };

            $scope.clientSelected = function(client) {
                if (client._selected && !client.manageId) {
                    var clientList = $scope.clientList;
                    var index = clientList.findIndex(function(x) { return x === client; });
                    var lastOrg = clientList.slice(0, index).reverse().find(function(x) { return x._selected && x.manageId; });
                    if (lastOrg != null) {
                        client.manageId = lastOrg.manageId;
                    }
                }
                else if (!client._selected) {
                    client.manageId = null;
                    client.manageName = null;
                }
            };
        });
})(angular);
