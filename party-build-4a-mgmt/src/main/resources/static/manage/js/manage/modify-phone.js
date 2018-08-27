(function () {
    toastr.options.positionClass = 'toast-middle-right';
    function refresh() {
        $('#yanzhengma').attr('src', 'validateCodeServlet?w=96&h=42&t=' + Math.random());
    };
    $('#modifyPhoneForm').parsley();
    window.refresh = refresh;
    $(window).on('load', function () {
        refresh();

        var $this = $(this);
        var countdown = $this.data('isLoading');
        var ditto = $this.data('ditto');


        //图片验证码
        $('[name="validateCode"]').on('blur', function () {
            if (!$(this).parsley().isValid()) {
                $(this).parsley().validate();
                return;
            }
            var code = $.trim($(this).val());
            $.get('/checkValidateCode/' + code + '/').then(function (data) {
                if(data){
                    $('#errvalidCode').addClass('hide');
                }else{
                    $('#errvalidCode').removeClass('hide');
                    $(this).val('');
                }
            });
        }),
            $('#sms-btn').on('click', function () {
                //校验新手机号是否已经注册
                var $validCode=$('[name="validateCode"]');
                var $newMobile = $('#newMobile');
                if (!$newMobile.parsley().isValid() || !$validCode.parsley().isValid()) {
                    $newMobile.parsley().validate();
                    $validCode.parsley().validate();
                    return;
                }
                var tel = $newMobile.val();
                if (tel != null || tel != '') {
                    $.get({
                        url: '/mgmt/checkTelephoneRegister/' + tel + '/',
                        cache: false
                    }).then(function (data) {
                        if (data) {
                            $('#errtelePhone').addClass('hide');
                            return $.get('/checkValidateCode/' + $.trim($validCode.val())+ '/');
                        }
                        $('#errtelePhone').removeClass('hide');
                        var def = $.Deferred();
                        setTimeout(function() {
                            def.reject();
                        }, 0);
                        return def.promise();
                    }).then(function(data){
                        if (data) {
                            sendMes();
                            $('#errvalidCode').addClass('hide');
                        }
                        else {
                            $('#errvalidCode').removeClass('hide');
                        }
                    });
                }
                //发送短信验证码
                function sendMes() {
                    if ($this.data('ditto')) {
                        return;
                    }
                    $this.data('ditto', true);
                    var tel = $('#newMobile').val();
                    $.ajax({
                        type: "POST",
                        url: '/sendMessage',
                        data: JSON.stringify({
                            telephone: tel,
                            type: '2003'
                        }),
                        headers: {
                            "content-type": "application/json"
                        }
                    }).then(function (data) {
                        if (data.flag == 'success') {
                            if ($this.data('isLoading') === undefined || $this.data('isLoading') === 0) {
                                countdown = 60;
                                $('#sms-btn').text(countdown + 's');
                                $this.data('isLoading', countdown);
                                var timer = setInterval(function () {
                                    countdown--;
                                    $this.data('isLoading', countdown);

                                    if (countdown === 0) {
                                        $('#sms-btn').text('重新发送');
                                        $this.data('ditto', false);
                                        clearInterval(timer);
                                    } else {
                                        $this.data('ditto', true);
                                        $('#sms-btn').text(countdown + 's');
                                    }
                                }, 1000);
                            }
                        } else {
                            //短信验证码发送失败
                            toastr.error(data.message);
                            $this.data('ditto', false);
                        }
                    },function(){
                        $this.data('ditto', false);
                    });
                };

            });

        //确认修改
        $('#verify').on('click', function () {
            var $modifyPhoneForm = $('#modifyPhoneForm');
            if (!$modifyPhoneForm.parsley().isValid()) {
                $modifyPhoneForm.parsley().validate();
                return;
            }
            var $thisData = $(this);
            if($thisData.data('submit')){
                return;
            }
            $thisData.data('submit',true);
            var obj = $.trim(JSON.stringify($modifyPhoneForm.serializeJSON()));
            $.ajax({
                type: "POST",
                url: '/manage/user/modifyMobile',
                data: obj,
                contentType: 'application/json',
                headers: {
                    "content-type": "application/json"
                }
            }).then(function (data) {
                $thisData.data('submit',false);
                if (data.flag == 'success') {
                    $this.data('ditto', false);
                    toastr.success("修改成功");
                    setTimeout(function () {
                        window.location = '/manage/worker-manage';
                    }, 2000);
                }
                if (data.flag == 'fail') {
                    if (data.message != '') {
                        toastr.error(data.message);
                    }
                    else {
                        toastr.warning('修改失败');
                        refresh();
                    }
                }
            },function(){
                $thisData.data('submit',false);
            });
        });

    });

})();