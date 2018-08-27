module.exports = {
    parserOptions: {
        ecmaVersion: 6,
        sourceType: 'script'
    },
    env: {
        browser: true,
        es6: true
    },
    globals: {
        angular: true,
        _: true
    },
    extends: [
        'eslint:recommended',
        '../.eslintrc.js'
    ],
    rules: {
        // 禁用无效的this
        'no-invalid-this': 0,

        // 保存 this 的变量名
        'consistent-this': [1, '_this', 'self', 'vm'],
    }
};
