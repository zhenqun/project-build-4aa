(function(angular) {
    angular.module('gbxx')
        .controller('SystemController', [
            '$scope', 
            '$uibModal',
            'toastrService',
            'systemService',
            'DEFAULT_PAGESIZE', 
            function($scope, $uibModal, toastrService, systemService, DEFAULT_PAGESIZE) {
                $scope.isLoading = false;
                $scope.queryParams = {
                    name: '',
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.totalItems = 0;
                $scope.adminList = [];

                this.$onInit = function() {
                    $scope.query();
                };

                $scope.beforeQuery = function(e) {
                    e.preventDefault();
                    $scope.queryParams.pageNo = 1;
                    $scope.query();
                }

                $scope.query = function() {
                    $scope.isLoading = true;

                    systemService.list($scope.queryParams)
                        .then(function(data) {
                            $scope.isLoading = false;
                            if (data != null) {
                                var error = data.error;
                                if (error != null) {
                                    toastrService.error(error);
                                    $scope.totalItems = 0;
                                    $scope.adminList = [];
                                }

                                $scope.totalItems = data.count;
                                $scope.adminList = data.data || [];                                                               
                            }
                        });
                };
                //删除
                function remove(ids) {
                    return systemService.remove(ids)
                        .then(function(data) {
                            if (data != null) {
                                var success = data.success;
                                if (!success) {
                                    toastrService.error('删除通知公告失败，请稍后重试。');
                                }
                            }
                            $scope.query();
                        });
                };
                //刪除
                $scope.removeSystem = function(system) {                 	
                    $uibModal.open({
                        size: 'sm',
                        templateUrl: 'remove-confirm.html'
                    }).result.then(function() {
                        remove([ system.adminId ]);
                    });
                };
                //批量刪除
                $scope.batchRemoveAdmin = function() {
                    var selected = $scope.adminList.filter(function(x) { return x._selected });                    
                    if (selected.length === 0) {
                        toastrService.warning('请选择至少一个通知公告。');
                        return;
                    }
                    $uibModal.open({
                        size: 'sm',
                        templateUrl: 'remove-confirm.html'
                    }).result.then(function() {
                        remove(selected.map(function(x) { return x.adminId }));
                    });
                };
                //编辑
                $scope.updata = function(system) {
                	$scope.system = system;
                	var edit = $uibModal.open({
                        size: 'sm',
                        templateUrl: 'update-confirm.html',
                        controller: 'updateCtrl',
                        resolve: {
                        	system: function () {
                              return $scope.system;
                            }                           
                          }
                    });
                	edit.result.then(function () {
                	      $scope.query();
                	});
                	
                };
                //新建
                $scope.addAdmin = function(){                	
                	var addsomeon= $uibModal.open({
                        size: 'sm',
                        templateUrl: 'add-confirm.html',
                        controller: 'addAdminCtrl'                        
                    });
                	addsomeon.result.then(function () {
                	      $scope.query();
                	});
                	
                };
                //重置密码
                $scope.restPwd = function(system) {                	
                	$scope.system = system;
                	var modalInstance = $uibModal.open({
                        size: 'sm',
                        templateUrl: 'restPwd-confirm.html',
                        controller: 'RestPwdCtrl',
                        resolve: {
                        	system: function () {
                              return $scope.system;
                            }                            
                          }
                    });
                	modalInstance.result.then(function () {
                		$scope.query();
	              	});
                	
                };
                
            }
        ])
    	.controller('RestPwdCtrl', function ($scope, $uibModalInstance, system, systemService, toastrService) {    		
    		$scope.passwordParams = {
    				username:system.adminName,    				
    				newPassword:''    				
    		}    		 	
    		$scope.restPassword = function() {
    			if ($scope.updateForm.$invalid){
    				return false; 
    			};
                return systemService.restPwd($scope.passwordParams)
                    .then(function(data) {
                        if (data != null) {                        	                            
                            $uibModalInstance.close(system);
                            if (data == false) {                            	
                                toastrService.error('修改密码失败，请稍后重试。');
                            }else{
                            	toastrService.success('修改密码成功');
                            }
                        }                       
                    });
            };           
    	})
    	.controller('updateCtrl', function ($scope, $uibModalInstance, system, systemService, toastrService) {
    		console.log(system.adminId);
    		$scope.updateParams = {
    				adminId:system.adminId,
    				adminName:system.adminName,
    				nickName:system.nickName,
    				version:system.version,    				
    				workDuty:system.workDuty,
    				workUnit:system.workUnit
    		};    		
    		$scope.editAdmin = function() {
    			if ($scope.editForm.$invalid){
    				return false; 
    			};
                return systemService.update($scope.updateParams)
                    .then(function(data) {
                        if (data != null) {
                            var success = data.success;
                            $uibModalInstance.close(system);
                            if (!success) {                            	
                                toastrService.error('编辑失败，请稍后重试。');
                            }else{
                            	toastrService.success('编辑成功！');
                            }
                        };                      
                    });               
            };    
    	})
    	.controller('addAdminCtrl', function ($scope, $uibModalInstance, systemService, toastrService) {    		
    		$scope.addAdeminParams = {
    				userId:'',
    				username:'',    				  				    				    				
    				password:'',
    				workUnit:'',
            		workDuty:''	
    		};    		        	  
    		$scope.addAdmin = function() {
    			if ($scope.addForm.$invalid){
    				return false; 
    			};
                return systemService.newadmin($scope.addAdeminParams)
                    .then(function(data) {
                        if (data != null) { 
                        	$uibModalInstance.close();
                            if (data.fail) {                            	
                                toastrService.error('新增失败，请稍后重试。');
                            };
                            if(data.success){
                            	toastrService.success('新增成功！');
                            };
                        };                      
                    });               
            };    
    	});
})(angular);














