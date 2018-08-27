/* 字典管理 */
var dictManager = (function(prefix) {

    var manager = cacheManager.create(prefix);

    //回溯树tree，寻找指定id的节点,若给定path，则path保存了路径
    manager.findTreeNode = function(id, tree, path) {
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

    manager.getDict = function(code) {
        var cache = this.get(code);
        if (cache) {
            return $.Deferred().resolve(cache);
        }
        var self = this;
        //获取某一字典集合下所有字典项，包括字典项子项
        return DDS.postJson("/JsTree/GetDictTree", {
            code: code
        }).done(function(data) {
            self.set(code, data);
        });
    };

    manager.getDictVal = function(code, value) {
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
            return this.getDict(code).then(function(data) {
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
    ins.code = option.code || this.attr('kt-dict');
    ins.value = option.val;
    ins.input = option.input;
    ins.root = option.root || this.attr('kt-dict-root');
    ins.enableSearch = false;
    ins.valueAttrs = {};
    ins.ele = this;
    ins.provider = null;
    ins.init();
    this.data('ktDict', ins);

    return this;
}
//获取数据
KtDict.Providers = {
    caches: [{
        match: /^zz$/i,
        json_data: function(code, value) {
            var self = this;
            return {
                progressive_render: false,
                data: function(node, success) {
                    var root, type;

                    if (node && node.id === '#') {
                        root = self.root;
                    } else {
                        root = node.id;
                    }
                    var treeOpt = {
                        root: root,
                        type: code,
                        withRoot: node.id === '#' ? 'true': 'false',
                        contents: 'zz'
                    };
                    if (node === -1 && self.value) {
                        treeOpt.openNodeId = self.value;
                        treeOpt.openNodeType = self.code;
                    }
                    if (self.keyword) {
                        treeOpt.keyword = self.keyword;
                    }

                    $.get('/sso/common/getOrganizations', {
                        orgId: root==null?'ED304F26-961B-4B33-ADB2-FBA342621BAE':root
                    },
                    'json').then(function(data) {
                    	
                        var data = data.map(function(x) {
                            return {
                                id: x.orgId,
                                parent: x.parentId || '#',
                                text: x.orgName,
                                state: {
                                    disabled: false,
                                    selected: false
                                },
                            	children: x.hasChildren==1?true:false
                            }
                        });
                        //jsTree对数据结构的要求：如果加载的数据已经有孩子节点，就不应该设置children=true;
                        if(treeOpt.withRoot === 'true'){
                        	delete data[0].children;
                        	data[0].state.opened = true;
                        }else{
                        	data.shift();
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
        setValue: function(val, type) {

            var ele = $(this.ele),
            input = this.option.input,
            code = this.code,
            value = this.value;
            if (!val) {
                ele.html('');
                if (input) {
                    $(input).val('');
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
                $.get('/sso/common/getOrganizations', {
                    orgId: val
                },
                'json').then(function(data) {
                    var data = data.map(function(x) {

                        return {
                            id: x.orgId,
                            parent: x.parentId || '#',
                            text: x.orgName,
                            state: {
                                opened: false,
                                disabled: false,
                                selected: true
                            },
                            children: x.hasChildren==1?true:false
                        }
                    });
                    if (data && data.length) {
                    	data = data[0];
                    }else{
                    	var ys ={
                                id: val,
                                text: $("#"+val + " a").text(),
                                state: {
                                    opened: false,
                                    disabled: false,
                                    selected: true
                                }
                            };
                    	data = ys;
                    }
                    if (!data) {
                        if (ele.is('input')) {
                            ele.val('');
                        } else {
                            ele.html('');
                        }
                        console.warn('dictionary not found! code ="' + code + '", value="' + val + '"');
                    } else {
                        if (ele.is('input')) {
                            ele.val(data.text);
                        } else {
                            ele.html(data.text);
                        }
                    }
                });
            }
        }
    },
    {
        match: /.*/,
        json_data: function(code, value) {
            var self = this;
            return {
                progressive_render: true,
                data: function(node, success) {
                    dictManager.getDict(code).done(function(data) {
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
        setValue: function(val, type) {
            var ele = $(this.ele),
            input = this.option.input,
            value = this.value;
            if (!val) {
                if (ele.is('input')) {
                    ele.val("");
                } else {
                    ele.html('');
                }
            } else {
                dictManager.getDictVal(type, val).done(function(data) {
                    if (!data) {

                        if (ele.is('input')) {
                            ele.val("");
                        } else {
                            ele.html('');
                        }
                        console.warn('dictionary not found! code ="' + type + '", value="' + val + '"');
                    } else {
                        if (ele.is('input')) {
                            ele.val(data.data.title);
                        } else {
                            ele.html(data.data.title);
                        }
                    }
                });
            }
        }
    }],
    getProvider: function(code) {
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
    setValue: function(val, type, triggerEvent) {
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

    bindDictTree: function(code, value, callback) {
        var self = this;
        $('#dict-selector').data('ktDict', self);

        var provider = this.provider;
        var json_data = provider.json_data.apply(this, arguments);
        
        var treeIns = $('#dict-tree').data('jstree');
        if(treeIns){
        	treeIns.destroy();
        }
        $('#dict-tree').jstree({
            'plugins': ["themes", "json_data", "ui", "contextmenu"],
            'core': {
                'data': json_data.data
            },
            'contextmenu': {
                items: function(node) {
                    return {};
                }
            }
        });

        $('#dict-tree').on("load_node.jstree",
        function(event, data) {
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
        function(event, data) {
            //选择事件。只有data.rslt.e != undefined的时候才是鼠标点击触发的
            if (data.event !== undefined) {
                var selectedValue = data.node.id;
                self.setValue(selectedValue, "ZZ", true);
                
                self.hideSelector();
            }
        });
    },

    hideSelector: function() {
        $("#dict-selector").hide();
        $("#org_id").val($("#org").data('ktDict').value);//固定赋值。。。
        var before = KtDict.showing;
        KtDict.showing = false;
        return before;
    },

    showSelector: function() {
        if (KtDict.showing === true) {
            return;
        }
        $("#dict-selector").show();
        var self = $("#dict-selector").data('ktDict');
        if (this.input && this.enableSearch) {
            $(this.input).keydown(function(event) {
                if (self.keywordTimer) {
                    clearTimeout(self.keywordTimer);
                }
                self.keywordTimer = setTimeout(function() {
                    self.keyword = $(self.input).val().replace(/\s/g, '');
                    self.keywordSearch();
                },
                500);
            });
        };
        KtDict.showing = true;
    },

    keywordSearch: function() {
        var keyword = this.keyword;
        if (keyword && keyword.length > 2) {
            $('#dict-tree').jstree('refresh');
            delete this['keyword'];
        }
    },

    init: function() {
        var code = this.code,
        value = this.value;

        this.provider = KtDict.Providers.getProvider(code);

        this.setValue(value, code);

        if (this.option.input) {
            var self = this;
            //字典选择的DIV
            if ($("#dict-selector").length === 0) {
                $(document.body).append('<div id="dict-selector" style="width:240px;z-index:99999;padding:3px;position:absolute;display:none;">' + '<div id="dict-tree" style="border:solid 1px #ccc;height: 300px;width:100%;background-color: #fff;overflow:auto;position:absolute;top:0px;left:0px;"></div>' + '</div>');
            }

            $(this.option.input).on('focus', null, {
                ktDict: self
            },
            function(e) {
                if (KtDict.showing === true && $("#dict-selector").data('ktDict') === e.data.ktDict) {
                    return;
                }
                var ktDict = e.data.ktDict,
                ele = $(this);
                var code = ktDict.code,
                value = ktDict.value;

                ktDict.bindDictTree(code, value,
                function() {
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
                    pop.css(offset);
                });
                $(this).select();
            }).on('blur', null, {
                ktDict: self
            },
            function(e) {
                var ktDict = e.data.ktDict,
                ele = $(this);
                if (ele.val() === '') {
                    ktDict.setValue('', '', true);
                }
            }).on('click',
            function(e) {
                e.stopPropagation();
            }).keydown(function(event) {
                if (event.keyCode === 9 || event.keyCode === 27) {
                    self.hideSelector();
                }
            });
        }
    }
};

$.fn.ktDict = KtDict;
$(document).on('click.ktdict.ktdoc',
function(e) {

    if (KtDict.showing && !$(e.target).is('#dict-selector, #dict-selector *')) {
        $('#dict-selector').data('ktDict').hideSelector();
    }
});