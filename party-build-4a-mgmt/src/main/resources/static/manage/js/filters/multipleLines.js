(function(angular) {
    angular.module('mgnt')
        .filter('multipleLines', function() {
            return function(str) {
                if (!str) {
                    return str;
                }
                return str.replace(/\n/g, '<br>');
            };
        });
})(angular);
