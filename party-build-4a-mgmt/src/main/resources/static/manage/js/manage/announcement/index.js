(function (angular) {
	angular.module('mgnt')
		.controller('AnnouncementController', [
			'$scope',
			'$http',
			'toastrService',
			'alert',
			'DEFAULT_PAGESIZE',
			function ($scope, $http, toastrService, alert, DEFAULT_PAGESIZE) {
			$scope.isLoading = true;
			$scope.isDeleteing = false;
			$scope.isPublishing = false;
			
			$scope.manipulate = {
				del: 'deleteItems',
				pub: 'publishItems'
			};
			
			$scope.params = {
				noticeTitle: null
			};
						
			$scope.page ={
				pageNo: 1,
				pageSize: DEFAULT_PAGESIZE
			};
			
			$scope.list = [];
			$scope.totalItems = 0;
			
			this.$onInit = function () {
				$scope.query();
			};

			$scope.beforeQuery = function() {
				$scope.page.pageNo = 1;
				$scope.query();
			};

			$scope.query = function () {
				var page = $scope.page;
				var params = $scope.params;
				var parameter = angular.merge({}, page, params);
				
				$scope.isLoading = true;
				$http.post('/manage/business/noticeQuery', parameter)
					.then(function (resp) {
						$scope.isLoading = false;
						
						if (!resp) {
							toastrService.error('未返回数据');
							return;
						}
						
						var result = resp;
						
						var resource = result.data || {};
						
						$scope.totalItems = resource.count || 0;
						
						$scope.list = resource.data || [];
					});
			};
			
			$scope.getItems = function (Flag, item) {
				var ids = [];
				
				if (item) {
					ids = [item.noticeId];
				} else {
					var list = $scope.list;
					
					ids = list.filter(function (item) {
						return item.selected;
					})
					.map(function (item) {
						return item.noticeId;
					});
				}
				
				if (ids.length === 0) {
					toastrService.error('请至少选择一项再进行操作');
					return;
				}
				
				$scope[Flag](ids);
			};
			
			$scope.publishItems = function (ids) {
				$scope.isPublishing = true;
				$http.post('/manage/business/releaseNotice', { ids: ids })
					.then(function (resp) {
						$scope.isPublishing = false;
						if (!resp) {
							toastrService.error('发布失败，请稍后重试');
							return;
						}
						
						$scope.page.pageNo = 1;
						$scope.query();
					});
			};
			
			$scope.deleteItems = function (ids) {
				alert({
					content: `确实要删除选中的${ids.length}条通知公告吗？（该操作不可恢复）`
				})
				.then(function() {
					$scope.isDeleteing = true;
					return $http.post('/manage/business/delNotice', { ids: ids });
				})
				.then(function (resp) {
					$scope.isDeleteing = false;
					if (!resp) {
						toastrService.error('删除失败，请稍后重试');
						return;
					}

					$scope.page.pageNo = 1;
					$scope.query();
				});
			};
		}]);
})(angular);