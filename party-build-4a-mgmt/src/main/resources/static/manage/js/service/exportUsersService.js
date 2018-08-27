(function (angular) {
    angular.module('mgnt')
        .factory('exportUsersService', function ($http, $uibModal, fileDownloadService, toastrService, MODAL_DIALOG_CONFIGS) {
            'ngInject';

            return {
                exportUser: function (url, userIds) {
                    var exportingModal = $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'exporting-modal.html'
                    }));
                    return $http.post(url, {ids:userIds})
                        .then(function (resp) {
                            var data = resp.data;
                            exportingModal.close();
                            if (data == null) {
                                toastrService.error('导出信息失败，请稍后重试');
                                return;
                            }
                            fileDownloadService.open(data);
                            return data;
                        });
                }
            }
        });
})(angular);
