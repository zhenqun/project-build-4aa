var cacheManager = {
    _caches: {},

    create: function (prefix, container) {
        var cache = this._caches[prefix];
        if (!cache) {
            var _prefix = prefix,
            _container = container || (window.localStorage || {});
            cache = this._caches[prefix] = {

                genKey: function (key) {
                    return _prefix + key.toUpperCase();
                },
                isKey: function (key) {
                    return (key.substring(0, _prefix.length) === _prefix);
                },

                get: function (key) {
                    var
                        k = this.genKey(key),
                        v = _container[k];
                    var val = (typeof (v) === "undefined" ? null : JSON.parse(v));
                    return val;
                },
                set: function (key, val) {
                    var k = this.genKey(key),
                        v = JSON.stringify(val);
                    _container[k] = v;
                    return this;
                },
                remove: function (key) {
                    var k = this.genKey(key);
                    delete _container[k];
                    return this;
                },
                clear: function () {
                    
                    for (k in _container) {
                        
                        if ((k in _container) && this.isKey(k)) {
                            delete _container[k];
                        }
                    }
                    return this;
                },
                sync: function (ver) {
                    var key = "_Expired";
                    var ver_old = this.get(key);
                    if (!ver_old || ver_old !== ver) {
                        this.clear();
                        this.set(key, ver);
                    }
                }
            };
        }
        return cache;
    },
    clear: function () {
        var cache;
        for (var k in this._caches) {
            cache = this._caches[k];
            cache.clear();
        }
    }
};