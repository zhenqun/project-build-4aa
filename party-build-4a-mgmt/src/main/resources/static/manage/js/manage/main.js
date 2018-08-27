
(function (angular, _) {
    angular.module('mgnt', [
        'ngAnimate',
        // 'ngTouch',
        'ngSanitize',
        'ngMessages',
        'ngFileUpload',
        'ui.bootstrap',
        'toastr',
        'angularMoment',
        'ADM-dateTimePicker',
        'selectize',
        'oitozero.ngSweetAlert'
        // 'angularMoment',
        // 'selectize'
    ]).provider('alert', function () {
        var defaultOption = {
            // showTitle: true,
            title: '操作确认',
            type: 'confirm',//tip,operate,confirm
            theme: 'normal',//danger
            reverse: false,
            template: false,
            timer: false,
            oktext: '确定',
            canceltext: '取消',
            size: 'sm',
            dialog: false
        };
        return {
            setDefaultOption: function (option) {
                defaultOption = angular.merge({}, defaultOption, option);
            },
            $get: function ($uibModal, $timeout, MODAL_DIALOG_CONFIGS) {
                'ngInject';

                return function (option) {
                    var opt = angular.extend({}, defaultOption, option);
                    var modalConfig = {
                        templateUrl: 'confirm.html',
                        animation: true,
                        controller: function ($scope, option) {
                            'ngInject';

                            $scope.option = option;

                            if ($scope.option.timer) {
                                $timeout(function () {
                                    $scope.$close();
                                }, $scope.option.timer);
                            }
                        },
                        resolve: {
                            option: function() { return opt; }
                        }
                    };

                    if (opt.dialog) {
                        modalConfig = angular.extend({}, modalConfig, MODAL_DIALOG_CONFIGS);
                    }

                    var modalInstance = $uibModal.open(modalConfig);
                    return modalInstance.result
                }

            }
        }
    })
    .run(function (moment, $rootScope, $http) {
        'ngInject';
        moment.locale('zh-cn');
        $rootScope.allAppRoles = [];
        $rootScope.clients = {
            isLoading: false
        }
        $rootScope.appList = [];
        $rootScope.selectClient = {};

        $rootScope.auths = [];
        $rootScope.authLoading = true;
        //获取权限
        $http.get('/manage/getAuth')
            .then(function (result) {
                $rootScope.authLoading = false;
                if (result.data) {
                    $rootScope.auths = result.data;
                }
            })

        $rootScope.isSecu = function () {
            return $rootScope.auths.find(function (x) { return x.authority == "ROLE_SECURITY" })
        }
        $rootScope.isAudit = function () {
            return $rootScope.auths.find(function (x) { return x.authority == "ROLE_AUDITOR" })
        }
    })
    .config(function (ADMdtpProvider, sweetAlertProvider) {
        'ngInject';

        ADMdtpProvider.setOptions({
            calType: 'gregorian',
            format: 'YYYY/MM/DD hh:mm',
            default: ''
        });
        sweetAlertProvider.setDefaults({
            confirmButtonColor: '#18a689',
            cancelButtonText: '取消',
            confirmButtonText: '确定'
        });
    })
    .filter('handleIndex', function () {
        return function (idx, page) {
            return idx + 1 + page.pageSize * (page.pageNo - 1);
        }
    })
    .config(function ($httpProvider, toastrConfig, uibPaginationConfig, alertProvider) {
        'ngInject';

        $httpProvider.defaults.headers.common = {
            'X-Requested-With': 'XMLHttpRequest'
        };

        angular.extend(uibPaginationConfig, {
            previousText: '«',
            nextText: '»'
        });

        angular.extend(toastrConfig, {
            newestOnTop: true,
            preventOpenDuplicates: true
        });

        alertProvider.setDefaultOption({
            type: 'confirm',
            theme: 'normal',
            reverse: false,
            template: false,
            timer: false,
            oktext: '确定',
            canceltext: '取消'
            // size: 'sm',
        })
    })
    .factory('_', function () {
        return _;
    });

})(angular, _);
