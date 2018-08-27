(function (angular) {
  angular.module('mgnt')
    /**
     * 默认的每页显示数据条数
     *
     * @type {Number}
     */
    .constant('DEFAULT_PAGESIZE', 15)

    .constant('RECORD_CODE', {
      sources: [
        { name: '互联网', val: '0' },
        { name: 'vpn业务管理员', val: '1' },
        { name: '安全管理端', val: '2' }
      ],
      types: [
        { name: '全部', val: '' },
        { name: '注册', val: '0' },
        { name: '登录', val: '1' },
        { name: '新增', val: '2' },
        { name: '设置管理范围', val: '3' },
        { name: '授权', val: '4' },
        { name: '禁用/开通', val: '5' },
        { name: '销', val: '6' },
        { name: '发送短信', val: '7' },
        { name: '重置密码', val: '8' },
        { name: '日志查询', val: '9' }
      ],
      logResult: [
        { name: '全部', val: '' },
        { name: '登陆成功', val: '1000' },
        { name: '登陆失败', val: '1001' }
      ],
      regResult: [
        { name: '全部', val: '' },
        { name: '注册成功', val: '1100' },
        { name: '注册失败', val: '1101' }
      ],
      authResult: [
        { name: '全部', val: '' },
        { val: '1200', name: '开通审计员' },
        { val: '1201', name: '开通业务管理员' },
        { val: '1202', name: '开通安全员' },
        { val: '1203', name: '禁用 / 启用审计员' },
        { val: '1204', name: '禁用 / 启用业务管理员' },
        { val: '1205', name: '禁用 / 启用安全员' },
        { val: '1206', name: '注销审计员' },
        { val: '1207', name: '注销业务管理员' },
        { val: '1208', name: '注销安全员' },
        { val: '1209', name: '导出审计员授权码' },
        { val: '1210', name: '导出业务管理员授权码' },
        { val: '1211', name: '导出安全员授权码' },
        { val: '1212', name: '设置审计员管理范围' },
        { val: '1213', name: '设置业务管理员管理范围' },
        { val: '1214', name: '设置安全员管理范围' },
        { val: '1215', name: '授权业务管理员' }
      ]
    })

    /**
    * 默认的每页显示提醒代办数据条数
    *
    * @type {Number}
    */
    .constant('ALERTS_DEFAULTSIZE', 20)

    /**
     * 聊天记录 图片消息  图片路径
     * 正式环境
     */
    .constant('IMG_PATH', 'http://mgnt.oss-cn-qingdao.aliyuncs.com/')

    /**
     * 聊天记录 图片消息  图片路径
     * 海南环境
     */
    //.constant('IMG_PATH', 'http://ido85hainan.oss-cn-qingdao.aliyuncs.com/')

    /** 
     * 最大字数限制
     * 
     * @type {Number}
     */
    .constant('MAXSIZE', 255)
    /**
     * uibootstrap 日期选择组件公共配置
     */
    .constant('DATEPICKER_CONFIG', {
      startingDay: 1,
      showWeeks: false,
      formatMonth: 'MM',
      formatYear: 'yyyy',
      formatDayHeader: 'EEE',
      formatDayTitle: 'yyyy-MM',
      minDate: new Date(2016, 1, 1)
    })
    /**
     * 常用selectize配置
     */
    .constant('SELECTIZE_CONFIGS', {
      wxs: {
        create: false,
        valueField: 'wxCodeId',
        labelField: 'wxName',
        searchField: 'wxName',
        maxItems: 1
      },
      employee: {
        create: false,
        valueField: 'userId',
        labelField: 'userName',
        searchField: 'userName' //,
        // plugins: ['remove_button']
      },
      wxGroup: {
        create: false,
        valueField: 'groupId',
        labelField: 'groupName',
        searchField: 'groupName',
        maxItems: 1
      }
    })
    /**
     * 图表默认使用的日期格式化字符串
     */
    .constant('CHART_DATE_FORMAT', 'YYYY/MM/DD')
    /**
     * 组织等级
     */
    .constant('ORG_LEVELS', {
      Sheng: 1,
      Shi: 2,
      Xian: 3
    })
    /**
     * 请求返回的结果常量
     */
    .constant('FLAGS', {
      Success: 'success',
      Fail: 'fail'
    })
    /**
     * 分隔符常量
     *
     * @type {Object}
     * @prop {RegExp} SPLITORS.Keyword 关键词的分隔符
     */
    .constant('SPLITORS', {
      Keyword: /[,，\n]/
    })
    /**
     * 模态框常量
     */
    .constant('MODAL_DIALOG_CONFIGS', {
      backdrop: 'static',
      keyboard: false
    })
    /**
     * 功能类型
     */
    .constant('FUNCTIONS_TYPE', {
      security: '安全员',
      auditor: '审计员',
      business: '管理员'
    })
    .run(function ($rootScope, DEFAULT_PAGESIZE, IMG_PATH) {
      'ngInject';

      $rootScope.DEFAULT_PAGESIZE = DEFAULT_PAGESIZE;
      $rootScope.IMG_PATH = IMG_PATH;
    });
})(angular);
