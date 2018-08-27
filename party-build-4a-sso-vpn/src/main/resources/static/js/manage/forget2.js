(function (window) {
    var userId = '';
    var mark = {isLoading: false};
    var $data = $(this);
    var dateMark = $data.data('isLoading');

    function display(e) {
        $('.area').each(function (index, item) {
            $(item).addClass('hide');
            $(e).removeClass('hide');
        });
    };

    $(window).on('load', function () {
        $('#firstForm').parsley();
        $('#secondForm').parsley();

        $('#sms-btn').on('click', function () {
            var $telePhone = $('[name="telePhone"]');
            var tel = $telePhone.val();
            if (!$telePhone.parsley().isValid()) {
                $telePhone.parsley().validate();
            }
            $.get('/sso/common/checkPhoneRegist/' + tel).then(function (data) {
                if (!data) {
                    $('#errtelePhone').removeClass('hide');
                    return;
                }
                else {
                    sendMes();
                }
            });
        });

        $('#firstForm').on('submit', function (e) {
            var $this = $(this);
            e.preventDefault();
            if ($this.data('isLoading')) {
                return;
            }
            if (!$this.parsley().isValid()) {
                $this.parsley().validate();
                return;
            }
            var yzm = $('#validateCode').val();
            var obj = JSON.stringify($.extend({}, $this.serializeJSON(), {verificationCode: $.trim(yzm)}));
            $this.data('isLoading', true);
            $.ajax({
                type: 'POST',
                contentType: "application/json",
                url: '/sso/verifyRetrievePwdVerificationCode',
                data: obj
            }).then(function (data) {
                $this.data('isLoading', false);
                if (data.flag == 'fail') {
                    toastr.error(data.message);
                    return;
                }
                else {
                    userId = data.message;
                    mark.isLoading = false;
                    display('#second');
                }
            }, function () {
                $this.data('isLoading', false);
            });

        });

        $('#secondForm').submit(function (e) {
            e.preventDefault();
            var $this = $(this);
            if ($this.data('isLoading')) {
                return;
            }
            $this.data('isLoading', true);
            if (!$this.parsley().isValid()) {
                $this.parsley().validate();
                return;
            }
            var password = $('[name="password"]').val();
            $.ajax({
                type: 'POST',
                contentType: "application/json",
                url: '/sso/retrievePasswordForWeb',
                data: JSON.stringify({
                    userId: userId,  //2259231949721600
                    password: password
                })
            }).then(function (data) {
                $this.data('isLoading', false);
                if (data.flag == 'fail') {
                    toastr.error(data.message);
                }
                else {
                    display('#third');
                }
            });
        });
    });

    //发送验证码
    var sendMes = function () {
        var tel = $('[name="telePhone"]').val();
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: '/sso/sendMessage',
            data: JSON.stringify({
                telephone: tel,
                type: 2001
            }),
            headers: {
                'content-type': 'application/json'
            }
        }).then(function (data) {

            if (data.flag == 'fail') {
                toastr.error(data.message);
            } else {
                $('#errtelePhone').addClass('hide');
                if (dateMark === undefined || dateMark === -1) {
                    var secLeft = 60;
                    $('#sms-btn').text(secLeft + 's');
                    dateMark = $data.data('isLoading', secLeft);
                    var timer = setInterval(function () {
                        secLeft--;
                        $('#sms-btn').text(secLeft + 's');
                        if (secLeft === 0) {
                            clearInterval(timer);
                            dateMark = $data.data('isLoading', -1);
                            $('#sms-btn').text('重新发送');
                        }
                    }, 1000);
                }
            }
        });
    };
})(window); // IIFE Immediate Invocation Function Expression
