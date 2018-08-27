(function(angular) {
    angular.module('mgnt')
        .controller('BusinessUploadController', function($scope, $uibModalInstance, Upload, _, selectedClient) {
            'ngInject';

            $scope.isUploading = false;
            $scope.uploadingProgress = 0;
            //批量导入
            $scope.uploadFiles = function (file) {
                if (file) {
                    $scope.isUploading = true;
                    $scope.uploadingProgress = 0;
                    file.upload = Upload.upload({
                        url: '/exportUserExcl',
                        data: {
                            file: file,
                            clientId: selectedClient.clientId
                        }
                    });

                    file.upload
                        .then(
                            function (response) {
                                $scope.isUploading = false;
                                var result = file.result = response.data;
                                if (result) {
                                    var client = [selectedClient.clientId];
                                    var businessUsers = result
                                        .map(function(x) {
                                            var user = {
                                                businessUserId: _.uniqueId('importedBusinessUser_'),
                                                businessUserName: x.relName,
                                                idCard: x.idCard,
                                                telephone: x.telephone,
                                                isExist: x.isExist === '1',
                                                clientList: angular.copy(client)
                                            };
                                            if (x.idCard != null) {
                                                user.businessUserId += x.idCard.slice(-4);
                                            }
                                            return user;
                                        });
                                    var users = businessUsers.filter(function(x) {
                                        return (!x.idCard) && (!x.businessUserName) && (!x.telephone);
                                    });
                                    businessUsers = businessUsers.filter(function(x) {
                                        return users.indexOf(x) === -1;
                                    });
                                    $uibModalInstance.close({
                                        users: businessUsers,
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
