Handlebars.registerHelper('pager', function (curPage, totalCount, pageSize) {
    totalCount = totalCount || 0;
    pageSize = pageSize || 50;
    var cur = curPage || 1,
        start, end, i,
        bef = false,
        aft = false,
        max = Math.ceil(totalCount / pageSize),
        end = max,
        canPre = cur > 1,
        canNext = cur < max,
        PAGE_TAG = 9;

    if (max <= PAGE_TAG) {
        start = 1;
    } else {
        start = cur - Math.floor(PAGE_TAG / 2);
        if (start <= 1) {
            start = 1;
        } else {
            bef = true;
        }
        end = start + PAGE_TAG - 1;
        if (end < max) {
            aft = true;
        } else {
            end = max;
        }
    }
    var data = {
        totalCount: totalCount,
        cur: cur,
        pages: [],
        bef: bef,
        aft: aft,
        max: max,
        canPre: canPre,
        canNext: canNext,
        pre: cur - 1,
        next: cur + 1
    };
    for (i = start; i <= end; i++) {
        data.pages.push({
            no: i,
            active: i === cur
        });
    }
    if(totalCount*1.0/pageSize>1){
    	var tpl = '<div class="eb-page-box"><ul class="eb-page clearfix">' +
        '<li {{#unless canPre}}disabled{{/unless}} cmd="page" {{#if canPre}}arg="{{pre}}"{{/if}}>上一页</li> ' +
        '{{#if bef}}<span>...</span> {{/if}}' +
        '{{#each pages}}<li {{#if active}}class="active"{{/if}} cmd="page" arg="{{no}}">{{no}}</li> {{/each}}' +
        '{{#if aft}}<span>...</span> {{/if}}' +
        '<li {{#unless canNext}}disabled{{/unless}}  cmd="page" {{#if canNext}}arg="{{next}}"{{/if}}>下一页</li> ' +
        '<li class="back-border-none">共{{totalCount}}条记录,分{{max}}页</li>' +
        '</ul></div>';
	    var pagerTpl = Handlebars.compile(tpl);
	    return new Handlebars.SafeString(pagerTpl(data));
    } 
});

Handlebars.registerHelper('pageNum', function (index, pageIndex, pageSize) {
    pageIndex = pageIndex || 1;
    pageSize = pageSize || 0;
    return index + (pageIndex - 1) * pageSize + 1;
});

Handlebars.registerHelper('date', function (date, format) {
    if (!date)
        return '';
    return '' + date.getFullYear() + '-'
        + (date.getMonth() >= 9 ? '' : '0') + (date.getMonth() + 1)
        + '-' + (date.getDate() >= 10 ? '' : '0') + date.getDate();
});

Handlebars.registerHelper('StrSub', function (str, length) {
    if (!str)
        return '';
    if (str.length <= length) {
        return str;
    } else {
        return str.substr(0,length-3)+"...";
    } 
});

Handlebars.registerHelper('FToDate', function (dateStr, format) { 
    if (!dateStr)
        return '';
    var date = new Date(dateStr);
    return '' + date.getFullYear() + '-'
        + (date.getMonth() >= 9 ? '' : '0') + (date.getMonth() + 1)
        + '-' + (date.getDate() >= 10 ? '' : '0') + date.getDate()
        + ' ' + (date.getHours() >= 10 ? '' : '0') + date.getHours()
        + ':' + (date.getMinutes() >= 10 ? '' : '0') + date.getMinutes()
});

Handlebars.registerHelper('FDate', function (date, format) { 
    if (!date)
        return '';
    return '' + date.getFullYear() + '-'
        + (date.getMonth() >= 9 ? '' : '0') + (date.getMonth() + 1)
        + '-' + (date.getDate() >= 10 ? '' : '0') + date.getDate()
        + ' ' + (date.getHours() >= 10 ? '' : '0') + date.getHours()
        + ':' + (date.getMinutes() >= 10 ? '' : '0') + date.getMinutes()
});
Handlebars.registerHelper('FToDate', function (dateStr, format) { 
    if (!dateStr)
        return '';
    var date = new Date(dateStr);
    return '' + date.getFullYear() + '-'
        + (date.getMonth() >= 9 ? '' : '0') + (date.getMonth() + 1)
        + '-' + (date.getDate() >= 10 ? '' : '0') + date.getDate()
        + ' ' + (date.getHours() >= 10 ? '' : '0') + date.getHours()
        + ':' + (date.getMinutes() >= 10 ? '' : '0') + date.getMinutes()
});
Handlebars.registerHelper('TDate', function (second) {
    var dateStr = "";
    
    var hour = Math.floor(second / 3600);
    var minutes = 0;
    if (hour > 0) {
        dateStr = (hour >= 10 ? '' : '0') + hour + ":";
    } else {
        dateStr = "00:";
    }
    second = second - hour*3600;
    minutes = Math.floor(second / 60);
    if (minutes > 0) {
        dateStr += (minutes >= 10 ? '' : '0') + minutes + ":";
    } else {
        dateStr += "00:";
    }
    second = second - minutes * 60;
    if (second > 0) {
        dateStr += (second >= 10 ? '' : '0') + second
    } else {
        dateStr += "00";
    }
     
    return dateStr;
});

Handlebars.registerHelper('niceDate', function (dateTime) {
    var dateNow = new Date();
    var timeSpan = dateNow - dateTime;
    var month = Math.floor(timeSpan / (1000 * 60 * 60 * 24 * 30));
    var day = Math.floor(timeSpan / (1000 * 60 * 60 * 24));
    var hour = Math.floor(timeSpan / (1000 * 60 * 60));
    var mins = Math.floor(timeSpan / (1000 * 60));
    if (month >= 1) {
        return '' + dateTime.getFullYear() + '-'
        + (dateTime.getMonth() >= 9 ? '' : '0') + (dateTime.getMonth() + 1)
        + '-' + (dateTime.getDate() >= 10 ? '' : '0') + dateTime.getDate();
    } else if (day >= 1) {
        return day + "天前";
    } else if (hour >= 1) {
        return hour + "小时前";
    } else if (mins >= 1) {
        return mins + "分钟前";
    } else {
        return "刚刚";
    }
});

Handlebars.registerHelper('number', function (val, digits, format10000) {
    var num = parseFloat(val),
        unit = '';
    if (isNaN(num)) {
        return '';
    }
    var oriNum = num + '';
    if (format10000 === true) {
        if (num >= 10000) {
            num /= 10000;
            unit = '万';
        }
        return new Handlebars.SafeString('<span title="' + oriNum + '">' + $.number(num, digits || 0, '.', '') + unit + '</span>');
    }
    return $.number(num, digits || 0, '.', '');

});

Handlebars.registerHelper('rights', function (rights, options) {
    var req = rights.split(','),
        curr = Context.user.Rights;

    var success = _.every(req, function (right) {
        return curr.indexOf(right) != -1;
    });

    if (success) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('datetime', function (date, format) {
    if (!date) {
        return '';
    }
    var dateObj = new Date(date);
    if (isNaN(dateObj.getDay())) {
        return '';
    }
    var mm = dateObj.getMonth() + 1,
        dd = dateObj.getDate();
    HH = dateObj.getHours();
    MM = dateObj.getMinutes();
    //return dateObj.getFullYear() + '-' + (mm > 9 ? mm : '0' + mm) + '-' + (dd > 9 ? dd : '0' + dd) + '  ' + (HH > 9 ? HH : '0' + HH) + ':' + (MM > 9 ? MM : '0' + MM);
    return dateObj.getFullYear() + '-' + (mm > 9 ? mm : '0' + mm) + '-' + (dd > 9 ? dd : '0' + dd) ;
    //return $.format.date(date, format);
});

Handlebars.registerHelper('isEmpty', function (value, placeHolder) {
    if (!!value) {
        return value;
    }
    return placeHolder;
});


(function (Handlebars) {
    // The module to be exported
    var helpers = {

        contains: function (str, pattern, options) {
            if (str.indexOf(pattern) !== -1) {
                return options.fn(this);
            }
            return options.inverse(this);
        },

        and: function (a, b, options) {
            if (a && b) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        gt: function (value, test, options) {
            if (value > test) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        gte: function (value, test, options) {
            if (value >= test) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        is: function (value, test, options) {
            if (value === test) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        isnt: function (value, test, options) {
            if (value !== test) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        lt: function (value, test, options) {
            if (value < test) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        lte: function (value, test, options) {
            if (value <= test) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        /**
         * Or
         * Conditionally render a block if one of the values is truthy.
         */
        or: function (a, b, options) {
            if (a || b) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        /**
         * ifNth
         * Conditionally render a block if mod(nr, v) is 0
         */
        ifNth: function (nr, v, options) {
            v = v + 1;
            if (v % nr === 0) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },

        /**
         * {{#compare}}...{{/compare}}
         *
         * @credit: OOCSS
         * @param left value
         * @param operator The operator, must be between quotes ">", "=", "<=", etc...
         * @param right value
         * @param options option object sent by handlebars
         * @return {String} formatted html
         *
         * @example:
         *   {{#compare unicorns "<" ponies}}
         *     I knew it, unicorns are just low-quality ponies!
         *   {{/compare}}
         *
         *   {{#compare value ">=" 10}}
         *     The value is greater or equal than 10
         *     {{else}}
         *     The value is lower than 10
         *   {{/compare}}
         */
        compare: function (left, operator, right, options) {
            /*jshint eqeqeq: false*/

            if (arguments.length < 3) {
                throw new Error('Handlebars Helper "compare" needs 2 parameters');
            }

            if (options === undefined) {
                options = right;
                right = operator;
                operator = '===';
            }

            var operators = {
                '==': function (l, r) {
                    return l == r;
                },
                '===': function (l, r) {
                    return l === r;
                },
                '!=': function (l, r) {
                    return l != r;
                },
                '!==': function (l, r) {
                    return l !== r;
                },
                '<': function (l, r) {
                    return l < r;
                },
                '>': function (l, r) {
                    return l > r;
                },
                '<=': function (l, r) {
                    return l <= r;
                },
                '>=': function (l, r) {
                    return l >= r;
                },
                'typeof': function (l, r) {
                    return typeof l == r;
                }
            };

            if (!operators[operator]) {
                throw new Error('Handlebars Helper "compare" doesn\'t know the operator ' + operator);
            }

            var result = operators[operator](left, right);

            if (result) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        },


        /**
         * {{if_eq}}
         *
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{if_eq this compare=that}}
         */
        if_eq: function (context, options) {
            if (context === options.hash.compare) {
                return options.fn(this);
            }
            return options.inverse(this);
        },

        /**
         * {{unless_eq}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{unless_eq this compare=that}}
         */
        unless_eq: function (context, options) {
            if (context === options.hash.compare) {
                return options.inverse(this);
            }
            return options.fn(this);
        },

        /**
         * {{if_gt}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{if_gt this compare=that}}
         */
        if_gt: function (context, options) {
            if (context > options.hash.compare) {
                return options.fn(this);
            }
            return options.inverse(this);
        },

        /**
         * {{unless_gt}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{unless_gt this compare=that}}
         */
        unless_gt: function (context, options) {
            if (context > options.hash.compare) {
                return options.inverse(this);
            }
            return options.fn(this);
        },

        /**
         * {{if_lt}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{if_lt this compare=that}}
         */
        if_lt: function (context, options) {
            if (context < options.hash.compare) {
                return options.fn(this);
            }
            return options.inverse(this);
        },

        /**
         * {{unless_lt}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{unless_lt this compare=that}}
         */
        unless_lt: function (context, options) {
            if (context < options.hash.compare) {
                return options.inverse(this);
            }
            return options.fn(this);
        },

        /**
         * {{if_gteq}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{if_gteq this compare=that}}
         */
        if_gteq: function (context, options) {
            if (context >= options.hash.compare) {
                return options.fn(this);
            }
            return options.inverse(this);
        },

        /**
         * {{unless_gteq}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{unless_gteq this compare=that}}
         */
        unless_gteq: function (context, options) {
            if (context >= options.hash.compare) {
                return options.inverse(this);
            }
            return options.fn(this);
        },

        /**
         * {{if_lteq}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{if_lteq this compare=that}}
         */
        if_lteq: function (context, options) {
            if (context <= options.hash.compare) {
                return options.fn(this);
            }
            return options.inverse(this);
        },

        /**
         * {{unless_lteq}}
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{unless_lteq this compare=that}}
         */
        unless_lteq: function (context, options) {
            if (context <= options.hash.compare) {
                return options.inverse(this);
            }
            return options.fn(this);
        },

        /**
         * {{ifAny}}
         * Similar to {{#if}} block helper but accepts multiple arguments.
         * @author: Dan Harper <http://github.com/danharper>
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{ifAny this compare=that}}
         */
        ifAny: function () {
            var argLength = arguments.length - 1;
            var content = arguments[argLength];
            var success = true;
            var i = 0;
            while (i < argLength) {
                if (!arguments[i]) {
                    success = false;
                    break;
                }
                i += 1;
            }
            if (success) {
                return content.fn(this);
            } else {
                return content.inverse(this);
            }
        },

        /**
         * {{ifEven}}
         * Determine whether or not the @index is an even number or not
         * @author: Stack Overflow Answer <http://stackoverflow.com/questions/18976274/odd-and-even-number-comparison-helper-for-handlebars/18993156#18993156>
         * @author: Michael Sheedy <http://github.com/sheedy> (found code and added to repo)
         *
         * @param  {[type]} context [description]
         * @param  {[type]} options [description]
         * @return {[type]}         [description]
         *
         * @example: {{ifEven @index}}
         */
        ifEven: function (conditional, options) {
            if ((conditional % 2) == 0) {
                return options.fn(this);
            } else {
                return options.inverse(this);
            }
        }

    };

    // Aliases
    helpers.ifeq = helpers.if_eq;
    helpers.unlessEq = helpers.unless_eq;
    helpers.ifgt = helpers.if_gt;
    helpers.unlessGt = helpers.unless_gt;
    helpers.iflt = helpers.if_lt;
    helpers.unlessLt = helpers.unless_lt;
    helpers.ifgteq = helpers.if_gteq;
    helpers.unlessGtEq = helpers.unless_gteq;
    helpers.ifLtEq = helpers.if_lteq;
    helpers.unlessLtEq = helpers.unless_lteq;


    for (var helper in helpers) {
        if (helpers.hasOwnProperty(helper)) {
            Handlebars.registerHelper(helper, helpers[helper]);
        }
    }
})(Handlebars);
