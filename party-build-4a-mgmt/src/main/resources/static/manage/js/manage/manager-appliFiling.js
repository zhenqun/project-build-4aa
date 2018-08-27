(function (angular){
    angular.module('mgnt')
        .controller('ManagerAppliController', function($scope, $http, $rootScope, toastrService, DEFAULT_PAGESIZE,assistManageService, $uibModal, alert, MODAL_DIALOG_CONFIGS, _, managerAppliFilingService){
            'ngInject';
            $scope.isLoading = false;
            $scope.queryParams = {
                relName: '',
                idCard: '',
                telephone: '',
                clientId: '',
                manageId: '',
                status: '1',
                pageNo: 1,
                pageSize:DEFAULT_PAGESIZE
            };
            $scope.params = {};

            $scope.itemIds = [];

            $scope.clients = [];
            $scope.list = [];
            $scope.totalItems = 0;

            this.$onInit = function() {
                $scope.applyParams();
                $scope.clientList();
                $scope.query();
            };

            $scope.applyParams = function() {
                $scope.params = angular.extend({}, $scope.queryParams);
            };

            $scope.beforeQuery = function() {
                $scope.queryParams.pageNo = 1;
                $scope.applyParams();

                $scope.query();
            };

            //查询该安全员能授权给辅助安全员的应用系统。
            $scope.clientList = function(){
                assistManageService.queryClients()
                    .then(function(data){
                    var data = data.data;
                    if(data){
                        $scope.clients = data;
                    }else{
                        $scope.clients = [];
                    }
                });
            };
            //备案查询
            $scope.query = function(){
                $scope.list = [];
                if($scope.isLoading){
                    return;
                }
                $scope.isLoading = true;
                //$scope.params = angular.extend({}, $scope.queryParams);
                managerAppliFilingService.query($scope.params)
                    .then(function(resp){
                        $scope.isLoading = false;
                        var data = resp.data;
                        if(data){
                            $scope.list = data.data || [];
                            $scope.totalItems = data.count;
                        }else{
                            $scope.list = [];
                            $scope.totalItems = 0;
                        }
                    })
                    .catch(function(error){
                        $scope.totalItems = 0;
                        toastrService.error(error);
                        $scope.isLoading = false;
                    });
            };

            $scope.beforeCheck = function (user) {
                var selectedUsers = [];
                if (user) {
                    selectedUsers = [user];
                }
                else {
                    selectedUsers = $scope.list.filter(function(x) { return x._selected; });
                    if (selectedUsers.length === 0){
                        toastrService.warning('请选择管理员');
                        return [];
                    }
                }

                return selectedUsers;
            };
            //备案否决
            $scope.reject = function(user){
                var selectedUsers = $scope.beforeCheck(user);
                if(!selectedUsers.length){
                    return;
                }
                var selectedUserIds = selectedUsers.map(function(x) { return x.itemId; });
                $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                    templateUrl: 'open-rejectReason-modal.html',
                    controller: 'OpenRejectReasonController',
                    resolve: {
                        selectedUserIds: function(){
                            return selectedUserIds;
                        }
                    }
                })).result.then(function() {
                    $scope.queryByStatus(1);
                });
            };

            //通过
            $scope.pass = function(user) {
                var selectedUsers = $scope.beforeCheck(user);
                if(!selectedUsers.length){
                    return;
                }
                $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                    templateUrl: 'pass.html',
                    controller: 'PassController',
                    resolve: {
                        selectedApplications: function() {
                            return selectedUsers;
                        }
                    }
                })).result.then(function() {
                    $scope.queryByStatus(1);
                });
            };

            //全部
            $scope.queryByStatus = function(state){
                $scope.totalItems = 0;
                $scope.params.status = state;
                $scope.queryParams.status = state;
                $scope.query();
            };
        })
        .controller('OpenRejectReasonController',function($scope, $http, $uibModalInstance, toastrService, FLAGS, selectedUserIds, managerAppliFilingService){
            'ngInject';

            $scope.isSubmitting = false;

            $scope.addReason = function(){
                var $form = $scope.rejectForm;
                if ($scope.isSubmitting) {
                    return;
                }

                if ($form.$invalid) {
                    $form.$setSubmitted();
                    return;
                }
                var items = selectedUserIds.map(function(x){
                    return {
                        itemId: x,
                        reason: $scope.reject.reason
                    }
                });
                $scope.isSubmitting = true;
                //否决
                managerAppliFilingService.reject({items:items})
                    .then(function(resp){
                        var result = resp.data;
                        if(result.flag === FLAGS.Success){
                            toastrService.success('否决成功');
                            $uibModalInstance.close(result);
                        }
                        else{
                            $scope.isSubmitting = false;
                            toastrService.error(result.message);
                        }
                    })
                    .catch(function(error){
                        $scope.isSubmitting = false;
                        toastrService.error(error);
                    });
            };
        })
        .controller('PassController', function ($scope, $http, toastrService, $uibModalInstance, FLAGS,selectedApplications, managerAppliFilingService) {
            'ngInject';

            var selectedApplications = selectedApplications;
            var result = [];
            this.$onInit = function() {

                $scope.selectedApplications = selectedApplications;
                $scope.check(selectedApplications);
            };

            $scope.check = function(selectedApplications) {
                var allItemIds = selectedApplications.map(function(x){
                    return x.itemId;
                });
                managerAppliFilingService.check({itemIds:allItemIds})
                    .then(function(resp){
                        var result = resp.data;
                        if(result.length){
                            $scope.selectedApplications.forEach(function(apply) {
                                var error = result.find(function(x) { return x.itemId === apply.itemId && x.flag === FLAGS.Fail; });
                                if (error != null) {
                                    apply.error = error;
                                }else{
                                    apply._checked = true;
                                }
                            });
                        }
                    })
                    .catch(function(error){
                        toastrService.error(error);
                    });

            };

            $scope.pass = function () {
                var itemIds = $scope.selectedApplications.filter(function (x) {
                    return x._checked;
                }).map(function(x){
                    return x.itemId;
                });
                if (itemIds == null || itemIds.length === 0) {
                    toastrService.warning('请选择校验状态正常的数据');
                    return;
                }
                managerAppliFilingService.pass({itemIds: itemIds})
                    .then(function (resp) {
                        var result = resp.data;
                        if (result.flag === FLAGS.Success) {
                            toastrService.success('审核成功');
                            $uibModalInstance.close();
                        } else {
                            toastrService.error(result.message);
                        }

                    })
                    .catch(function (error) {
                        toastrService.error(error);
                    });
            }
    });
})(angular);