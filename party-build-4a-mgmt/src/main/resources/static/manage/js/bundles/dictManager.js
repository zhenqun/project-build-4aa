
/**
 * 折叠树，返回 tree 中 parentId 与 parentNode 相同的节点数组。可以进行递归调用，将平铺的树结构折叠。
 *
 * @param tree
 * @param parentNode
 * @returns {*}
 */
function collapseOrgTree(tree, parentNode) {
    if (tree == null) {
        return [];
    }
    var levelTree = tree.filter(function(x) {
        return (!x.parentId && !parentNode)
            || (x.parentId != null && x.parentId.trim() === parentNode);
    });
    levelTree = levelTree.map(function(x) {
        var children = collapseOrgTree(tree, x.orgId);
        if (children == null || children.length === 0) {
            children = false;
        }
        return {
            id: x.orgId,
            code: x.code,
            text: x.orgName,
            children: children,
            state: {
                opened: true
            },
            type: 'org'
        };
    });
    //levelTree.sort(function(item1, item2) {
    //    return item1.sort - item2.sort;
    //});
    return levelTree;
}

function getOuName(org, orgs) {
    if (!org) {
        return '';
    }
    if (org.ouName) {
        return org.ouName;
    }
    return getOuName(orgs.find(function(x) { return x.orgId === org.parentId; }), orgs);
}

/**
 * 获取原始数据的 extData
 * @param {Object} treeNode 原始数据
 * @param {Array.<String>} extDataFields extData 中包含的属性，可以使用 attrName=treeNodeAttrName 来指定属性的别名。
 *
 * @example 原始数据中的 level，在 extData 中保存为 treeLevel，可以通过指定 ['treeLevel=level'] 实现。
 * @returns {Object} 转换后的树节点的 extData
 */
function getExtData(treeNode, extDataFields) {
    if (typeof extDataFields === 'undefined') {
        extDataFields = ['code', 'treeLevel=level','type'];
    }

    var extData = {};
    var extDataFieldsCount = extDataFields.length;
    for (var i = 0; i < extDataFieldsCount; i++) {
        var extDataField = extDataFields[i];
        var treeNodeKey = extDataField;
        var originalKey = extDataField;
        if (extDataField.indexOf('=') > -1) {
            var keys = extDataField.split('=');
            treeNodeKey = keys[0];
            originalKey = keys[1];
        }
        extData[treeNodeKey] = treeNode[originalKey];
    }

    return extData;
}

/* 字典管理 */
var dictManager = (function (prefix) {

    var manager = cacheManager.create(prefix);

    //回溯树tree，寻找指定id的节点,若给定path，则path保存了路径
    manager.findTreeNode = function (id, tree, path) {
        var i, val;
        for (i = tree.length - 1; i >= 0; i--) {
            if (path) {
                path.push(tree[i]);
            }
            if (tree[i].attr.id === id) {
                return tree[i];
            }
            if (tree[i].children) {
                val = arguments.callee(id, tree[i].children, path);
                if (val) {
                    return val;
                }
            }
            if (path) {
                path.pop();
            }
        }
    };

    manager.getDict = function (code) {
        var cache = this.get(code);
        if (cache) {
            return $.Deferred().resolve(cache);
        }
        var self = this;
        //获取某一字典集合下所有字典项，包括字典项子项
        return DDS.postJson("/JsTree/GetDictTree", {
            code: code
        }).done(function (data) {
            self.set(code, data);
        });
    };

    manager.getDictVal = function (code, value) {
        if (value) {
            //临时缓存(匿名对象)，刷新页面就销毁！
            var _prefix = this.genKey(code + "_temp"),
                _manager = cacheManager.create(_prefix, {});
            var key = value,
                val = _manager.get(key);
            if (val) {
                return $.Deferred().resolve(val);
            }
            var self = this;
            return this.getDict(code).then(function (data) {
                var v = self.findTreeNode(value, data);
                _manager.set(key, v);
                return v;
            });
        }
        return $.Deferred().resolve();
    };

    return manager;
})("DICT_");

// ktDict 字典选择控件
function KtDict(option, val) {
    if ($.type(option) === 'undefined') {
        return;
    }
    var ins;
    if ($.type(option) === 'string') {
        ins = this.data('ktDict');
        return ins[option](val);
    }
    ins = new KtDict();

    ins.option = option;
    ins.url = option.url;
    ins.clientId = option.clientId;
    ins.code = option.code || this.attr('kt-dict');
    ins.value = option.val;
    ins.input = option.input;
    ins.root = option.root || this.attr('kt-dict-root');
    ins.enableSearch = option.enableSearch || (typeof this.attr('kt-enable-search') !== 'undefined');
    ins.valueAttrs = {};
    ins.ele = this;
    ins.provider = null;
    /* 2017-06-18 WheelJS 添加是否允许选择顶级节点 */
    ins.allowTop = option.allowTop;
    /* 2017-06-18 WheelJS 添加是否允许选择顶级节点 */
    var treeLevel = option.treeLevel;
    if (typeof treeLevel === 'undefined') {
        treeLevel = this.attr('kt-tree-level');
    }
    ins.treeLevel = treeLevel;
    ins.init();
    this.data('ktDict', ins);

    return this;
}
//获取数据

KtDict.Providers = {
    caches: [{
        match: /^zz$/i,
        json_data: function (code, value) {
            var self = this;
            return {
                progressive_render: false,
                data: function (node, success) {
                    var root, type;

                    if (node && node.id === '#') {
                        root = self.root;
                    } else {
                        root = node.id;
                    }
                    var treeOpt = {
                        root: root,
                        type: code,
                        withRoot: node.id === '#' ? 'true' : 'false',
                        contents: 'zz'
                    };
                    if (node === -1 && self.value) {
                        treeOpt.openNodeId = self.value;
                        treeOpt.openNodeType = self.code;
                    }
                    if (self.keyword) {
                        treeOpt.keyword = self.keyword;
                    }

                    $.get(self.url, {
                        clientId: self.clientId == null ? "" : self.clientId,
                        orgId: root == null ? '' : root,
                        treeLevel: self.treeLevel,
                        keyword: self.keyword
                    },
                        'json').then(function (data) {
                            if (self.keyword) {
                                if (data == null || data === '') {
                                    $('#dict-search-tips').text('搜索结果过多，请提供更加精确的关键词').removeClass('hide');
                                }
                                else if (data.length === 0) {
                                    $('#dict-search-tips').text('没有查询到您要的结果').removeClass('hide');
                                }
                                else {
                                    data = collapseOrgTree(data, '');
                                }
                            }
                            else {
                                data = data.map(function (x) {
                                    var disabled = false;
                                    if (self.allowTop === false) {
                                        disabled = x.parentId == null || x.parentId === '#';
                                    }
                                    var treeNode = {
                                        id: x.orgId,
                                        parent: x.parentId || '#',
                                        text: x.orgName,
                                        code: x.code,
                                        treeLevel: x.level,
                                        state: {
                                            disabled: disabled,
                                            selected: false
                                        },
                                        extData: getExtData(x),
                                        children: x.hasChildren == '1' ? true : false
                                    };
                                    var ouName = x.ouName;
                                    if (ouName == null) {
                                        ouName = getOuName(x, data);
                                        if ((!ouName)
                                            && node.original != null
                                            && node.original.extData != null
                                        ) {
                                            ouName = node.original.extData.ouName;
                                        }

                                    }
                                    treeNode.extData.ouName = ouName;
                                    return treeNode;
                                });
                                //jsTree对数据结构的要求：如果加载的数据已经有孩子节点，就不应该设置children=true;
                                if (treeOpt.withRoot === 'true') {
                                    delete data[0].children;
                                    data[0].state.opened = true;
                                } else {
                                    data.shift();
                                }
                            }
                            success(data);
                            var tree = $('#dict-tree');
                            tree.data("dict-path", undefined);
                            if (value) {
                                tree.jstree("open_node", "#" + value);
                            }
                        });
                }
            };
        },
        setValue: function (val, type) {
            var self = this;

            var ele = $(this.ele),
                input = this.option.input,
                code = this.code,
                value = this.value;
            var displayValue = this.displayValue;
            var node = arguments[arguments.length - 1];
            if (!val) {
                this.displayValue = null;
                ele.html('');
                if (input) {
                    $(input).val('');
                }
                if (displayValue != this.displayValue) {
                    ele.trigger('displayValueChange.ktDict', { name: this.displayValue, code: null, treeLevel: this.treeLevel });
                }
            } else {
                //TODO：根据code和value设置值
                //根据code和value设置对象值
                //                    var treeOpt = {
                //                        root: val,
                //                        type: type,
                //                        withRoot: "true",
                //                        contents: "zz",
                //                        openNodeId: val,
                //                        openNodeType: type
                //                    };
                // 先清空，避免出现Id与其他字段无法对应的问题
                ele.trigger('displayValueChange.ktDict', { name: null, code: null, treeLevel: null });
                $.get(self.url, {
                    clientId: self.clientId == null ? "" : self.clientId,
                    orgId: val
                },
                    'json')
                    .then(function (data) {
                        var data = data.map(function (x) {
                            var treeNode = {
                                id: x.orgId,
                                parent: x.parentId || '#',
                                code: x.code,
                                text: x.orgName,
                                treeLevel: x.level,
                                state: {
                                    opened: false,
                                    disabled: false,
                                    selected: true
                                },
                                extData: getExtData(x, ['code', 'treeLevel=level', 'type', 'ouName']),
                                children: x.hasChildren == 1 ? true : false
                            };
                            if (treeNode.extData.ouName == null) {
                                if (node != null && node.original != null) {
                                    var extData = node.original.extData;
                                    if (extData != null) {
                                        treeNode.extData.ouName = extData.ouName;
                                    }
                                }
                            }
                            return treeNode;
                        });
                        if (data && data.length) {
                            data = data[0];
                        } else {
                            var ys = {
                                id: val,
                                text: $("#" + val + " a").text(),
                                state: {
                                    opened: false,
                                    disabled: false,
                                    selected: true
                                }
                            };
                            data = ys;
                        }
                        if (!data) {
                            self.displayValue = null;
                            if (ele.is('input')) {
                                ele.val('');
                            } else {
                                ele.html('');
                            }
                            console.warn('dictionary not found! code ="' + code + '", value="' + val + '"');
                        } else {
                            self.displayValue = data.text;
                            if (ele.is('input')) {
                                ele.val(data.text);
                            } else {
                                ele.html(data.text);
                            }
                        }
                        if (displayValue != self.displayValue) {
                            ele.trigger('displayValueChange.ktDict', {
                                name: data.text,
                                code: data.code,
                                treeLevel: data.treeLevel,
                                extData: data.extData
                            });
                        }
                    });
            }
        }
    },
    {
        match: /.*/,
        json_data: function (code, value) {
            var self = this;
            return {
                progressive_render: true,
                data: function (node, success) {
                    dictManager.getDict(code).done(function (data) {
                        var path = [];
                        if (self.root) {
                            data = dictManager.findTreeNode(self.root, data);
                        }
                        dictManager.findTreeNode(value, data, path);
                        $('#dict-tree').data("dict-path", path);
                        success(data);
                    });
                }
            };
        },
        setValue: function (val, type) {
            var self = this;

            var ele = $(this.ele),
                input = this.option.input,
                value = this.value;
            var displayValue = this.displayValue;
            if (!val) {
                this.displayValue = null;
                if (ele.is('input')) {
                    ele.val("");
                } else {
                    ele.html('');
                }
                if (displayValue != this.displayValue) {
                    ele.trigger('displayValueChange.ktDict', { name: this.displayValue, code: null, treeLevel: this.treeLevel });
                }
            } else {
                ele.trigger('displayValueChange.ktDict', { name: null, code: null, treeLevel: null });
                dictManager.getDictVal(type, val).done(function (data) {
                    if (!data) {
                        self.displayValue = null;
                        if (ele.is('input')) {
                            ele.val("");
                        } else {
                            ele.html('');
                        }
                        console.warn('dictionary not found! code ="' + type + '", value="' + val + '"');
                    } else {
                        self.displayValue = data.text;
                        if (ele.is('input')) {
                            ele.val(data.text);
                        } else {
                            ele.html(data.text);
                        }
                    }
                    if (displayValue != self.displayValue) {
                        ele.trigger('displayValueChange.ktDict', { name: data.text, code: data.code, treeLevel: data.treeLevel });
                    }
                });
            }
        }
    }],
    getProvider: function (code) {
        var provider;
        for (var i = 0,
            j = this.caches.length; i < j; i++) {
            provider = this.caches[i];
            if (provider.match.test(code)) {
                break;
            }
        }
        return provider;
    }
};
KtDict.prototype = {
    /**
     * 根据配置的 enableSearch 更新视图
     *
     * @author WheelJS
     * 2017-08-14
     */
    updateView: function(enableSearch) {
        var defaultSize = {
            width: 300,
            height: 350
        };
        this.enableSearch = enableSearch;
        var size = $.extend({}, defaultSize, JSON.parse(localStorage['dict.size'] || '{}'));
        $('#dict-tree')
            .css({
                'border': '',
                'height': (size.height - 41) + 'px'
            });
        if (enableSearch) {
            $('#dict-search')
                .css('padding-right', '')
                .find('button[type="submit"]')
                .removeClass('hide');
            $('#dict-search-text')
                .attr('placeholder', '输入至少两个字符搜索')
                .css('cursor', '')
                .prop('readonly', false);
        }
        else {
            $('#dict-search')
                .css('padding-right', '19px')
                .find('button[type="submit"]')
                .addClass('hide');
            $('#dict-search-text')
                .attr('placeholder', '不支持搜索')
                .css('cursor', 'not-allowed')
                .prop('readonly', true);
        }
    },

    setValue: function (val, type, triggerEvent, node) {
        var provider = this.provider;
        provider.setValue.apply(this, arguments);
        if (val !== this.value) {
            //仅当实际的修改发生时（从界面选取等）时，才触发change事件。初始化值时不应该触发事件。
            if (triggerEvent) {
                $(this.ele).trigger('change.ktDict', val);
            }
            this.value = val;
        }
    },
    setUrl: function (url) {
        this.url = url;
    },
    bindDictTree: function (code, value, callback) {
        var self = this;
        $('#dict-selector').data('ktDict', self);

        var provider = this.provider;
        var json_data = provider.json_data.apply(this, arguments);

        var treeIns = $('#dict-tree').data('jstree');
        if (treeIns) {
            treeIns.destroy();
        }
        $('#dict-tree').jstree({
            'plugins': ["themes", "json_data", "ui", "contextmenu"],
            'core': {
                'data': json_data.data
            },
            'contextmenu': {
                items: function (node) {
                    return {};
                }
            }
        });

        $('#dict-tree').on("load_node.jstree",
            function (event, data) {
                if (self.value) {
                    var tree = $("#dict-tree");
                    var path = tree.data("dict-path");
                    while (path && path.length > 1) {
                        tree.jstree("open_node", $("#dict-tree #" + path.shift().attr.id), false, true);
                    }
                    tree.jstree("select_node", $("#dict-tree #" + value));
                }
                if (callback) {
                    callback();
                }
            }).on("select_node.jstree",
            function (event, data) {
                //选择事件。只有data.rslt.e != undefined的时候才是鼠标点击触发的
                if (data.event !== undefined) {
                    var selectedValue = data.node.id;
                    self.setValue(selectedValue, "ZZ", true, data.node);

                    self.hideSelector();
                }
            });

        // 创建DOM并绑定事件时获取的 self 不见得是要显示的self！！！
        $('#dict-search')
            .off('submit')
            .on('submit', null, { ktDict: self }, function(e) {
                e.preventDefault();
                e.data.ktDict.keyword = $('#dict-search-text').val().replace(/\s/g, '');
                e.data.ktDict.keywordSearch();
            })
            .find('.js-clear')
            .off('click')
            .on('click', null, { ktDict: self }, function(e) {
                e.preventDefault();
                delete e.data.ktDict.keyword;
                e.data.ktDict.keywordSearch();
            });
    },
    hideSelector: function () {
        $("#dict-selector").hide();
        // $("#org_id").val($("#org").data('ktDict').value);//固定赋值。。。
        var before = KtDict.showing;
        KtDict.showing = false;
        // 隐藏时还原搜索框状态
        delete this.keyword;
        $('#dict-search .js-clear').addClass('hide');
        $('#dict-search-text').val('');
        $('#dict-search-tips').text('').addClass('hide');
        return before;
    },

    showSelector: function () {
        if (KtDict.showing === true) {
            return;
        }
        $("#dict-selector").show();
        var self = $("#dict-selector").data('ktDict');
        KtDict.showing = true;
        this.updateView(this.enableSearch);
    },

    keywordSearch: function () {
        var keyword = this.keyword;
        $('#dict-search-tips').text('').addClass('hide');
        if (keyword && keyword.length >= 2) {
            // 搜索前隐藏提示框
            $('#dict-search .js-clear').removeClass('hide');
            // delete this['keyword'];
        }
        else {
            $('#dict-search-text').val('');
            $('#dict-search .js-clear').addClass('hide');
        }
        $('#dict-tree').jstree('refresh');
    },

    init: function () {
        var self = this;
        var code = this.code,
            value = this.value;

        this.provider = KtDict.Providers.getProvider(code);

        this.setValue(value, code);

        if (this.option.input) {
            //字典选择的DIV
            if ($("#dict-selector").length === 0) {
                var defaultSize = {
                    width: 300,
                    height: 350
                };
                var size = $.extend({}, defaultSize, JSON.parse(localStorage['dict.size'] || '{}'));
                var selectorHtml = [
                    '<div id="dict-selector" style="width:' + size.width + 'px;height:' + size.height + 'px;z-index:99999;position:absolute;display:none;background-color:#fff;border-radius:3px;">',
                        '<div class="e-tree-container" style="border:1px solid #ccc;border-radius:3px;overflow:hidden;">',
                            '<form id="dict-search" class="e-searchbar-holder">',
                                '<input type="text" id="dict-search-text">',
                                '<button type="button" class="sift-btn hide js-clear" title="清空关键字" style="height: 32px;right: 32px;"><i class="fa fa-remove"></i></button>',
                                '<button type="submit" class="sift-btn" title="搜索" style="height:32px;"><i class="fa fa-search"></i></button>',
                            '</form>',
                            '<div id="dict-search-tips" class="hide" style="background-color: #fff;font-size: 14px;text-align: center;padding-top: 20px;"></div>',
                            '<div id="dict-tree" style="border:1px solid #ccc;height:100%;width:100%;background-color:#fff;overflow:auto;"></div>',
                        '</div>',
                    '</div>'
                ];

                $(document.body).append(selectorHtml.join(''));

                var $selector = $('#dict-selector');
                if (typeof $selector.resizable === 'function') {
                    $selector.resizable({
                        ghost: true,
                        minWidth: 200,
                        minHeight: 250,
                        maxWidth: 550,
                        maxHeight: 400
                    }).on('resize', _.debounce(function(e, ui) {
                        var size = ui.size;
                        if (localStorage) {
                            localStorage['dict.size'] = JSON.stringify(size);
                        }
                    }, 300));
                }
                this.updateView(this.enableSearch);
            }

            $(this.option.input).on('focus', null, {
                ktDict: self
            },
                function (e) {
                    if (KtDict.showing === true && $("#dict-selector").data('ktDict') === e.data.ktDict) {
                        return;
                    }
                    var ktDict = e.data.ktDict,
                        ele = $(this);
                    var code = ktDict.code,
                        value = ktDict.value;

                    ktDict.bindDictTree(code, value,
                        function () {
                            var offset = ele.offset();
                            self.showSelector();
                            var pop = $("#dict-selector");
                            var poph = pop.outerHeight();
                            var inputH = ele.outerHeight();
                            if (document.body.offsetHeight < (offset.top + inputH + poph)) {
                                offset.top -= poph;
                            } else {
                                offset.top += inputH;
                            }
                            if (offset.top < 0) {
                                offset.top = 0;
                            }
                            pop.css(offset);
                        });
                    $(this).select();
                }).on('blur', null, {
                    ktDict: self
                },
                function (e) {
                    var ktDict = e.data.ktDict,
                        ele = $(this);
                    if (ele.val() === '') {
                        ktDict.setValue('', '', true);
                    }
                }).on('click',
                function (e) {
                    e.stopPropagation();
                }).keydown(function (event) {
                    if (event.keyCode === 9 || event.keyCode === 27) {
                        self.hideSelector();
                    }
                });
        }
    }
};

$.fn.ktDict = KtDict;
$(document).on('click.ktdict.ktdoc',
    function (e) {

        if (KtDict.showing && !$(e.target).is('#dict-selector, #dict-selector *')) {
            $('#dict-selector').data('ktDict').hideSelector();
        }
    });