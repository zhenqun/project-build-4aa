(function (angular) {
    var PUBLISHFLAGS = {
        'publish': '1',
        'save': '0'
    };

    angular.module('mgnt')
        .controller('RecordController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            'toastrService',
            'dataIsland',
            'DEFAULT_PAGESIZE',
            '$http',
            'moment',
            'RECORD_CODE',
            
            function ($scope, $window, $timeout, $uibModal, toastrService, dataIsland, DEFAULT_PAGESIZE, $http, moment, RECORD_CODE) {
                this.$onInit = function () {

                };
                $scope.isCollapsed = true;
                $scope.isLoading = false;
                $scope.page = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.detailList = [];
                $scope.recordList = [];
                $scope.queryParams = {
                    startDate: '',
                    endDate: '',
                    logOperator: '',
                    logType: ''
                };
                $scope.totalItems = '';
                $scope.page = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.localData = RECORD_CODE;
                var handleDate = function (date) {
                    if (date && date != '') {
                        return moment(date).format('YYYY-MM-DD HH:mm:ss');
                    }
                    else {
                        return ''
                    }
                }
                $scope.query = function () {
                    $scope.isLoading = true;
                    params = angular.merge({}, $scope.queryParams, $scope.page)
                    params.startDate = handleDate(params.startDate);
                    params.endDate = handleDate(params.endDate);
                    if (params.startDate && params.endDate && (params.startDate === params.endDate)) {
                        params.startDate = moment(params.startDate).startOf('days').format('YYYY-MM-DD HH:mm:ss');
                        params.endDate = moment(params.endDate).endOf('days').format('YYYY-MM-DD HH:mm:ss');
                    }
                    $http.post('/manage/audit/grantlogQuery', params)
                        .then(function (data) {
                            $scope.isLoading = false;
                            if (data.data) {
                                $scope.recordList = data.data.data;
                                $scope.totalItems = data.data.count;
                            }
                        })
                }
                $scope.query();

                $scope.openDetail = function (record) {
                    var record = record;
                    var modalInstance = $uibModal.open({
                        templateUrl: 'detail.html',
                        animation: true,
                        size: 'lg',
                        controller: function ($scope) {
                            $scope.queryDetail = function () {
                                // record.showDetail = !record.showDetail;
                                $scope.detailLoading = true;
                                var id = record.logId.toString();
                                $http.post('/manage/audit/logdetailQuery/' + id)
                                    .then((function (data) {
                                        $scope.detailLoading = false;
                                        if (data.data) {
                                            $scope.detailList = data.data;
                                        }
                                    }));
                            }
                            $scope.queryDetail();

                        },
                        resolve: {

                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.detailList = [];
                    }, function () {
                        $scope.detailList = [];
                    });
                }
            }
        ]);
})(angular);