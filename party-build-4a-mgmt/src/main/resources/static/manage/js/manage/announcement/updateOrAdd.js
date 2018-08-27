(function (angular) {
	angular.module('mgnt')
		.controller('UpdateController', function ($scope, $http, $window, $timeout, toastrService) {
			var idFlag = 'add';
			
			$scope.params = {
				noticeTitle: null,
				noticeContent: null
			};

			$scope.isLoading = false;
			
			this.$onInit = function () {
				var id = $("#current").val();
				
				if (id !== idFlag && id != null && id.length > 0) {
					$scope.params.noticeId = id;
					$scope.query();
				}
			};
			
			$scope.query = function () {
				var params = $scope.params;
				
				$http.get('/manage/business/noticeDetail/' + params.noticeId)
					.then(function (resp) {
						if (!resp) {
							toastrService.error('未返回数据');
							return;
						}
						
						var result = resp.data || {};

						angular.extend($scope.params, result);
						if (result != null) {
							$window.getEditor()
								.then(function(editor) {
									editor.setContent(result.noticeContent);
								});
						}
					});
			};
			
			$scope.addOrEdit = function () {
				$scope.isLoading = true;
				var params = angular.copy($scope.params);
				$window.getEditor()
					.then(function(editor) {
						params.noticeContent = editor.getContent();
						return $http.post('/manage/business/saveOrupdateNotice', params);
					})
					.then(function (resp) {
						if (!resp) {
							toastrService.error('公告处理失败，请稍后重试');
							return;
						}
						
						toastrService.success('公告添加成功，正返回列表...');
						
						$timeout(function () {
							$window.location.href = "/manage/announcement";
						}, 500);
					});
			};
		});
})(angular);