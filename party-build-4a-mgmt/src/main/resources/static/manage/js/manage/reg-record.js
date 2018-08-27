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
                $scope.recordList = [];
                $scope.queryParams = {
                    startDate: '',
                    endDate: '',
                    logOperator: '',
                    result: '',
                    logFrom: '0'
                };
                $scope.totalItems = '';
                $scope.page = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.localData = RECORD_CODE;
                var handleDate = function(date){
                    if(date&&date!='') {
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
                    if(params.startDate&&params.endDate&&(params.startDate === params.endDate)) {
                         params.startDate = moment(params.startDate).startOf('days').format('YYYY-MM-DD HH:mm:ss');
                         params.endDate = moment(params.endDate).endOf('days').format('YYYY-MM-DD HH:mm:ss');
                    }                  
                    $http.post('/manage/audit/registerlogQuery', params)
                        .then(function (data) {
                            $scope.isLoading = false;
                            if (data.data) {
                                $scope.recordList = data.data.data;
                                $scope.totalItems = data.data.count;
                            }
                        })
                }
                $scope.query();
            }
        ]);
})(angular);