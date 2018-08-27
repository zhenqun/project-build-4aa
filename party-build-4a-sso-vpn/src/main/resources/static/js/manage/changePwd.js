(function(angular) {
    angular.module('gbxx')
        .controller('cPwdController', [
            '$scope', 
            '$uibModal',
            'toastrService',
            'systemService',
            'DEFAULT_PAGESIZE',                   
            function($scope, $uibModal, toastrService, systemService, DEFAULT_PAGESIZE) {
            	$scope.querypararm = {
            			oldPassword:'',
            			newPassword:''
            	};
      
                $scope.changePwd = function() {
                	if ($scope.newPwdForm.$invalid){
        				return false; 
        			};
                	return systemService.modifyPwd($scope.querypararm)
	                    .then(function(data) {	                      
	                        if (data != null) {	                            
	                            if (data == false) {
	                                toastrService.error("修改密码失败");	                               
	                            }else{
	                            	toastrService.success('修改密码成功，下次请使用新密码登录！');
	                            }	                                                           
	                        }
	                    });
                };
                
            }
        ]);
})(angular);














