// JavaScript Document
var resize = function() {
	//高度修改
	$(".e-xh").each(function() {
		var xh = $(this).closest('.e-ph');
		var xHeight = xh.height();
		xHeight = xHeight - parseInt($(this).css("margin-top")) - parseInt($(this).css("margin-bottom"));
		/*-parseInt(xh.css("padding-top"))-parseInt(xh.css("padding-bottom"))*/
		$(this).css({
			"height": xHeight + "px"
		});
	});
	$(".e-xw").each(function() {
		var xw = $(this).closest('.e-pw');
		$(this).css({
			"width": xw.width() + "px"
		});
	});

	$(".e-xh-main").each(function() {
		var xHeight = $(this).parent().height();
		$(this).siblings().each(function() {
			if ($(this).is(":visible")) {
				xHeight -= $(this).outerHeight();
				// alert(xHeight);
			}
		});
		$(this).height(xHeight);
	});

	$(".e-xw-main").each(function() {
		var xWidth = $(this).parent().width();
		$(this).siblings().each(function() {
			if ($(this).is(":visible")) {
				xWidth -= $(this).outerWidth();
			}
		});
		$(this).width(xWidth);
	});
	$(".e-xh-main2").each(function() {
		var xHeight = $(this).parent().height();
		$(this).siblings().each(function() {
			if ($(this).is(":visible")) {
				xHeight -= $(this).outerHeight();
			}
		});
		$(this).height(xHeight);
	});

	$(".e-xw-main2").each(function() {
		var xWidth = $(this).parent().width();
		$(this).siblings().each(function() {
			if ($(this).is(":visible")) {
				xWidth -= $(this).outerWidth();
			}
		});
		$(this).width(xWidth);
	});

	$(".e-xh2").each(function() {
		var xh = $(this).closest('.e-ph');
		var xHeight = xh.height();
		xHeight = xHeight - parseInt($(this).css("margin-top")) - parseInt($(this).css("margin-bottom"));
		$(this).css({
			"height": xHeight + "px"
		});
	});
	$(".e-xw2").each(function() {
		var xw = $(this).closest('.e-pw');
		$(this).css({
			"width": xw.width() + "px"
		});
	});

	$(".efl-10").each(function() {
		var h = $(this).height();
		var w = $(this).width();
		if ($(this).parent().hasClass("e-clip-v")) {
			$(this).css({
				"height": h + "px"
			});
			//$(this).css({"width":w+"px"});
			$(this).removeClass('efl-10');
			$(this).addClass('flex-point');
		} else {
			$(this).css({
				"width": w + "px"
			});
			$(this).removeClass('efl-10');
			$(this).addClass('flex-point');
		}
	})
	$(".flex-point").each(function() {
		$(this).addClass('efl-10');
		var h = $(this).height();
		var w = $(this).width();
		if ($(this).parent().hasClass("e-clip-v")) {
			$(this).css({
				"height": h + "px"
			});
			//$(this).css({"width":w+"px"});
			$(this).removeClass('efl-10');
			$(this).addClass('flex-point');
		} else {
			$(this).css({
				"width": w + "px"
			});
			$(this).removeClass('efl-10');
			$(this).addClass('flex-point');
		}
		$(this).removeClass('efl-10');
	})
};

$("a[data-toggle='tab']").click(function(){
  resize();
})