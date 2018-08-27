(function(angular) {
    angular.module('mgnt')
        .service('assistManageService', function($http) {
            return {
                query: function(params) {
                    return $http.post('/assistSecurity/getAllAssistUser', params);
                },
                queryClients: function() {
                    return $http.post('/assistSecurity/getAvaliableClients');
                },
                add: function(assistor) {
                    let params = assistor;
                    if (!Array.isArray(assistor)) {
                        params = [assistor];
                    }
                    return $http.post('/assistSecurity/addAssistUser', params);
                },

                /**
                 * 重置 VPN 密码
                 * @param assistor
                 * @returns {HttpPromise}
                 */
                resetVpn: function(assistor) {
                    return $http.post('/assistSecurity/resetVpnPassword', {
                        fzuserId: assistor.fzuserId
                    });
                },

                /**
                 * 设置辅助安全员启用/禁用
                 * @param params
                 * @returns {HttpPromise}
                 */
                setState: function(params) {
                    return $http.post('/assistSecurity/updateAssistState', params);
                },

                /**
                 * 撤销辅助安全员
                 * @param params
                 */
                revoke: function(fzuserIds) {
                    return $http.post('/assistSecurity/cancelAssistUser', {
                        fzuserIds: fzuserIds
                    });
                },

                /**
                 * 查询辅助安全员的应用和管理范围
                 * @param fzuserId
                 * @returns {HttpPromise}
                 */
                getAssistorClients: function(fzuserId) {
                    return $http.post('/assistSecurity/getAssistClientAndManage', {
                        fzuserId: fzuserId
                    });
                },

                /**
                 * 保存辅助安全员的应用和管理范围
                 * @param params
                 * @returns {HttpPromise}
                 */
                saveAssistorClients: function(params) {
                    let data = params;
                    if (!Array.isArray(params)) {
                        data = [params];
                    }
                    return $http.post('/assistSecurity/grantAssistUser', data);
                }
            };
        });
})(angular);
