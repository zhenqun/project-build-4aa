'use strict';

(function (angular) {
    angular.module('mgnt').directive('multipleList', function () {
        'ngInject';

        return {
            restrict: 'A',
            priority: 100,
            require: 'ngModel',
            link: function link(scope, element, attr, ctrl) {
                var match = /\/(.*)\//.exec(attr.multipleList);
                var separator = match && new RegExp(match[1]) || attr.multipleList || ',';
                // const ngList = element.attr(attr.$attr.multipleList) || ', ';
                var trimValues = attr.ngTrim !== 'false';
                // const separator = trimValues ? trim(ngList) : ngList;

                var parse = function parse(viewValue) {
                    // If the viewValue is invalid (say required but empty) it will be `undefined`
                    if (angular.isUndefined(viewValue)) return;

                    var list = [];

                    if (viewValue) {
                        viewValue.split(separator).forEach(function (value) {
                            if (value) {
                                list.push(trimValues ? value.trim() : value);
                            };
                        });
                    }

                    return list;
                };

                ctrl.$parsers.push(parse);
                ctrl.$formatters.push(function (value) {
                    if (angular.isArray(value)) {
                        return value.join(',');
                    }

                    return undefined;
                });

                // Override the standard $isEmpty because an empty array means the input is empty.
                ctrl.$isEmpty = function (value) {
                    return !value || !value.length;
                };
            }
        };
    });
})(angular);