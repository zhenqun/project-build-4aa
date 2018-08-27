(function(angular) {
    angular.module('gbxx')
        .value('dataHandler', function(resp) {
            return resp.data;
        })
        .constant('DEFAULT_PAGESIZE', 20)
        /**
         * 试题类型常量
         * 
         * @prop {string} EXAM_TYPES.Single 单选
         * @prop {string} EXAM_TYPES.Multiple 多选
         * @prop {string} EXAM_TYPES.Judgment 判断
         */
        .constant('EXAM_TYPES', {
            Single: '0',
            Multiple: '1',
            Judgment: '2'
        });
})(angular);