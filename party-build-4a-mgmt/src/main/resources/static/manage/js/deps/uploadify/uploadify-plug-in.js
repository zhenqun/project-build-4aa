(function ($) {
    var Uploadify = {
        popWindow: function (fileQueue, buttonID) {
            toastr.options = {
                "closeButton": true,
                "debug": false,
                "positionClass": "toast-bottom-right",
                "onclick": null,
                "showDuration": "300",
                "hideDuration": "1000",
                "timeOut": "5000",
                "extendedTimeOut": "1000",
                "showEasing": "swing",
                "hideEasing": "linear",
                "showMethod": "fadeIn",
                "hideMethod": "fadeOut"
            }
            toastr.options = {
                "closeButton": true,
                "debug": false,
                "positionClass": "toast-bottom-right",
                "onclick": null,
                "showDuration": "300",
                "hideDuration": "1000",
                "timeOut": "5000",
                "extendedTimeOut": "1000",
                "showEasing": "swing",
                "hideEasing": "linear",
                "showMethod": "fadeIn",
                "hideMethod": "fadeOut"
            }
            toastr.success('上传成功');
            //$("#" + fileQueue).dialog({
            //    title: "上传进程",
            //    width: 320,
            //    height: 230,
            //    bgiframe: true,
            //    resizable: false,
            //    modal: false,
            //    hide: true,
            //    show: true,
            //    position: ['right', 'bottom'],
            //    buttons: [{
            //        text: "上传",
            //        id: "btn-upload",
            //        click: function () {
            //            $("#" + buttonID).uploadify("upload", "*");
            //        }
            //    },
            //    {
            //        text: "取消",
            //        id: "btn-delete",
            //        click: function () {
            //            $("#" + buttonID).uploadify("cancel", "*");
            //            //$("#" + opts.queueID).html("");
            //        }
            //    },
            //    {
            //        text: "关闭",
            //        id: "btn-delete",
            //        click: function () {
            //            $(this).dialog("close");
            //        }
            //    }
            //    ],
            //    close: function (event, ui) {
            //        if ($("#btn-delete").html() == "关闭") {
            //            $(this).dialog("close");
            //        }
            //    }
            //});

        },
        errorMessageShow: function (htmlID, message) {
            toastr.options = {
                "closeButton": true,
                "debug": false,
                "positionClass": "toast-bottom-right",
                "onclick": null,
                "showDuration": "300",
                "hideDuration": "1000",
                "timeOut": "5000",
                "extendedTimeOut": "1000",
                "showEasing": "swing",
                "hideEasing": "linear",
                "showMethod": "fadeIn",
                "hideMethod": "fadeOut"
            }
            toastr.error('上传失败，' + message);
            //$("#" + htmlID).html(message);
            //$("#" + htmlID).dialog({
            //    title: "提示",
            //    width: 320,
            //    height: 230,
            //    bgiframe: true,
            //    resizable: false,
            //    modal: true,
            //    buttons: [{
            //        text: "确定",
            //        id: "btn-upload",
            //        click: function () {
            //            $(this).dialog("close");
            //        }
            //    }],
            //    close: function (event, ui) {
            //        if ($("#btn-delete").html() == "关闭") {
            //            $(this).dialog("close");
            //        }
            //    }
            //});
        }
    }

    $.fn.extend({
        ktUploadify: function (buttonID, options) {
            var params = {
                swf: "/manage/js/deps/uploadify/uploadify.swf",
                uploader: "/UploadHandler.ashx",
                queueID: "uploadify-fileQueue",
                buttonText: "上传...",
                auto: true,//选中的文件是否直接自动上传
                queueSizeLimit: 0,//某一次上传数量最大限制(0表示不限制）
                uploadLimit: 0,//最大总上传数量限制(0表示不限制）
                fileSizeLimit: 0,//某一次上传文件的大小限制,默认单位是kb，0表示不限制
                width: 120,
                height: 30,
                buttonClass: "some-class",
                buttonCursor: "arrow",
                multi: true,
                removeCompleted: false,
                fileTypeExts: "*.*",
                progressData: "percentage",
                itemTemplate: '<div id="${fileID}" class="uploadify-queue-item">\
                <div class="cancel">\
                    <a href="javascript:$(\'#${instanceID}\').uploadify(\'cancel\', \'${fileID}\')">X</a>\
                </div>\
                <span class="fileName">${fileName} (${fileSize})</span><span class="data"></span>\
            </div>',
                'overrideEvents': ['onSelectError', 'onUploadError', 'onDialogClose'],
                onQueueComplete: function (queueData) {
                    var t = setTimeout(function () {
                        $("#" + opts.queueID).dialog("close");
                    }, 3000);
                    $("#" + opts.queueID).mouseover(function () {
                        clearTimeout(t)
                    }).mouseout(function () {
                        t = setTimeout(function () {
                            $("#" + opts.queueID).dialog("close");
                        }, 3000);
                    });
                    
                },
                onDialogClose: function (queueData) {
                    
                    if (queueData.filesSelected > 0 && queueData.filesErrored == 0) {

                        Uploadify.popWindow(opts.queueID, buttonID);
                        if (opts.auto) {
                            $("#btn-upload").remove();
                        }
                    }
                },
                onSelectError: function (file, errorCode, errorMsg) {
                    
                    var u = $("#uploadify");
                    switch (errorCode) {
                        case -100:
                            Uploadify.errorMessageShow(errorHtml, "上传的文件数量已经超出系统限制文件个数！");
                            break;
                        case -110:
                            Uploadify.errorMessageShow(errorHtml, "文件 [" + file.name + "] 大小超出系统限制的" + u.uploadify('settings', 'fileSizeLimit') + "大小！");
                            break;
                        case -120:
                            Uploadify.errorMessageShow(errorHtml, "文件 [" + file.name + "] 大小异常！");
                            break;
                        case -130:
                            Uploadify.errorMessageShow(errorHtml, "文件 [" + file.name + "] 类型不正确！");
                            break;
                        default:
                            Uploadify.errorMessageShow(errorHtml, "上传出现未知错误，请联系技术人员！");
                            break;
                    }
                }
            };
            var opts = $.extend({}, params, options);

            //创建错误信息提示框
            //var errorHtml = "uploadify-error";
            //$("#" + errorHtml).remove();
            //$("body").append("<div id='" + errorHtml + "'></div>");
            //if ($("#" + buttonID).length == 0) {
            //    Uploadify.errorMessageShow(errorHtml, "找不到指定的上传按钮：" + buttonID);
            //    return false;
            //};
            //创建进程显示框
            //$("#" + opts.queueID).remove();
            //$("body").append('<div id="' + opts.queueID + '" class="e-hidden e-auto"></div>');

            $("#" + buttonID).uploadify(opts);

        }
    });
})(jQuery);