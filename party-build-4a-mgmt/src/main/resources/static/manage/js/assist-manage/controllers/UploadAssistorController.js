(function(angular) {
    angular.module('mgnt')
        .controller('UploadAssistorController', function($scope, $uibModalInstance, Upload, _) {
            'ngInject';

            $scope.isUploading = false;
            $scope.uploadingProgress = 0;

            // 批量导入
            $scope.uploadFiles = function (file) {
                if (file) {
                    $scope.isUploading = true;
                    $scope.uploadingProgress = 0;
                    file.upload = Upload.upload({
                        url: '/importAssistor',
                        data: {
                            file: file
                        }
                    });

                    file.upload
                        .then(
                            function (response) {
                                $scope.isUploading = false;
                                var result = file.result = response.data;
                                if (result) {
                                    var assistors = result
                                        .map(function(x) {
                                            var assistor = {
                                                fzuserId: _.uniqueId('importedAssistor_'),
                                                relName: x.relName,
                                                idCard: x.idCard,
                                                telephone: x.telephone,
                                                remark: x.remark,
                                                isExist: x.isExist === '1'
                                            };
                                            if (x.idCard != null) {
                                                assistor.fzuserId += x.idCard.slice(-4);
                                            }
                                            return assistor;
                                        });
                                    var users = assistors.filter(function(x) {
                                        return (!x.relName) && (!x.idCard) && (!x.telephone);
                                    });
                                    assistors = assistors.filter(function(x) {
                                        return users.indexOf(x) === -1;
                                    });
                                    $uibModalInstance.close({
                                        assistors: assistors,
                                        rawUsers: users
                                    });
                                }
                            },
                            function (response) {
                                $scope.isUploading = false;
                                $uibModalInstance.dismiss(response);
                            },
                            function (evt) {
                                $scope.uploadingProgress = file.progress = Math.min(
                                    100,
                                    parseInt(100.0 * evt.loaded / evt.total)
                                );
                            }
                        );
                }
            };
        });
})(angular);
