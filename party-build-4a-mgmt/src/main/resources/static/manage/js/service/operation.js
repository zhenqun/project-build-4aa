(function (angular) {
    angular.module('mgnt')
        .service('resultHandlerService', function (toastrService) {
            'ngInject';

            //fn为列表查询函数
            this.resultHandler = function (fn) {
                return function (result) {
                    if (result.data) {
                        toastrService.success('操作成功');
                        fn();
                        return;
                    }
                    toastrService.error('操作失败');
                }
            };
        });
}(angular));
