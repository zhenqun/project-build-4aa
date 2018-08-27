(function(angular) {
    angular.module('mgnt')
        .filter('toNo', function() {
            return function(index, pageNo, pageSize) {
                var beginIndex = ((pageNo - 1) * pageSize) + 1;
                return beginIndex + index;
            };
        });
})(angular);
