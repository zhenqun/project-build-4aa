/**
 * 将data-island的json转换成js Object。
 *
 * @author WheelJS
 */
(function (angular) {
    var noop = function noop(obj) {
        return obj;
    };

    angular.module('mgnt')
        .provider('dataIslandProvider', function () {
            var _this = this;

            /**
             * 可以设置转换函数，在转换成Object后会调用转换函数。
             */
            this.formatters = {};

            this.addFormatter = function (key, formatter) {
                _this.formatters[key] = formatter;
            };

            this.hasFormatter = function (key) {
                return _this.formatters.hasOwnProperty(key);
            };

            this.getFormatter = function (key) {
                if (key == null) {
                    return noop;
                }
                if (_this.hasFormatter(key)) {
                    return _this.formatters[key];
                }
                return noop;
            };

            this.$get = function () {
                return _this;
            };
        })
        .service('dataIsland', function (dataIslandProvider) {
            'ngInject';

            var islandData = {};
            var islands = Array.from(angular.element('[data-island]'));
            islands.forEach(function (island) {
                var $island = angular.element(island);
                var name = angular.element.camelCase($island.attr('data-island'));
                var json = $island.html();

                if (name != null && json != null && json.length > 0) {
                    var obj = angular.fromJson(json);
                    if (dataIslandProvider.hasFormatter(name)) {
                        obj = dataIslandProvider.getFormatter(name)(obj);
                    }
                    islandData[name] = obj;
                }
            });
            return islandData;
        });
})(angular);
