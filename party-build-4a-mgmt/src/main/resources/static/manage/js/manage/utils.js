(function($) {
    var PASSWORD_REG = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,18}$/;
    if (typeof Parsley !== 'undefined') {
        Parsley.addValidator('passwordStrength', {
            requirementType: 'string',
            validateString: function(val) {
                return PASSWORD_REG.test(val);
            }
        });
    }

    var WHITESPACE_REG = /\s+/g;

    /**
     * 去掉对象中字段的空格，可以通过配置去掉所有空白（无论是两头还是中间），或者忽略字段。
     * @param {object} obj 对象中所有字段默认会调用 trim 去掉两头空格
     * @param options
     * @param {Array.<string>} options.ignore 忽略字段的 key，不对这些字段做任何处理
     * @param {Array.<string>} options.trimAll 不仅去掉两头空格，同时去掉中间空格的 key
     * @returns {*}
     */
    window.trimField = function(obj, options) {
        var opts = $.extend({
            ignore: [],
            trimAll: []
        }, options);
        for (var key in obj) {
            if (obj.hasOwnProperty(key) && opts.ignore.indexOf(key) === -1) {
                var value = obj[key];
                if (typeof value === 'string') {
                    if (opts.trimAll.indexOf(key) > -1) {
                        value = value.replace(WHITESPACE_REG, '');
                    }
                    else {
                        value = $.trim(value);
                    }
                    obj[key] = value;
                }
            }
        }
        return obj;
    }
})(jQuery);
