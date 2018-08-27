$(function() {
	$(".nav li").hover(function() {
		$(this).css("background-image","none");
		$(this).prev("li").css("background-image","none");
	},function() {
		$(this).prev("li").css("background-image","url(img/right-b.png)");
		$(this).css("background-image","url(img/right-b.png)");
		$(".nav li").last().css("background-image","");
	})
	
	$(".face-box .btn-play").click(function() {
		var srctxt = $(this).parents('.face-box').find('.face-img').attr("src");
		if( srctxt == ""){
			$(this).css("display","none");	
		}else{
			$(this).parents('.face-box').css("display","none");
			$(".show_video").css("display","block");
			$(this).parents('.face-box').next('.show_video').show('fast');
		}
	});
	$(".item-con.zb .btn-play").click(function() {
		$(this).parents('.face-box').css("display","none");
		$(".show_video").css("display","block");
		$(this).parents('.face-box').next('.show_video').show('fast');
		
		$(".item-con.zb .face-img").css("display","none");
		$(".item-con.zb .btn-play").css("display","none");
		$(".item-con.zb .show_video").css("display","block");
	});
	
	
	
	if($(window).width() <= 1000) {
//	侧滑
		$("#m-btn-nav").click(function() {
			if ($("#left-close").val() == 0) {
				$("body").css("transform", "translateX(80%)");
				// $(".site-search").css("display", "none");
				$(".m-nav").css("left", "-80%");
				$("#left-close").val(1);
			} else {
				$("body").css("transform", "none");
				$(".m-nav").css("left", "-100%");
				$("#left-close").val(0);
			}
		});
		
	//	导航下拉
		$(".m-nav-item").click(function() {
			$(this).next().slideToggle("hide");
		});
		$(".m-nav-close").click(function() {
			$("body").css("transform", "none");
			$("#left-close").val(0);
		});
		
	//手机端搜索
		$("#m-btn-search").on('click', function() {
			var $searchList = $(".search_list").toggle("fast");
			if ($searchList.css('display') == 'block') {
				$(this).hide();
			} else {
				$(this).show();
			}
			return false;
		});
		$(".m-soso").on('click', function() {
			var $searchList1 = $(".study-form").toggle("fast");
			if ($searchList1.css('display') == 'block') {
				$(this).hide();
				$('.tab-bar.study').css('margin-top', '55px');
			} else {
				$(this).show();
				$('.tab-bar.study').css('margin-top', '15px');
			}
			return false;
		});
		
		$("body").click(function(e) {
			var $searchList = $('.search_list');
			if ($searchList.css('display') == 'block') {
				if (!$(e.target).closest('form').hasClass('search_list')) {
					$searchList.hide('fast');
					$('#m-btn-search').show();
				}
			}
	//		var $searchList1 = $('.study-form');
	//		if ($searchList1.css('display') == 'block') {
	//			if (!$(e.target).closest('form').hasClass('study-form')) {
	//				$searchList.hide('fast');
	//				$('.m-soso').show();
	//			}
	//		}
		});
		$(window).on('scroll', function() {
			$('#nav-list').css('display', 'none');
			$("#m-btn-search").show();
			$('.search_list').hide('fast');
			
			$(".m-soso").show();
			$('.study-form').hide('fast');
			$('.tab-bar.study').css('margin-top', '15px');
		});
	}
});
//$(function(){
//  $('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
//  $('.tree li.parent_li > span').on('click', function (e) {
//      var children = $(this).parent('li.parent_li').find(' > ul > li');
//      if (children.is(":visible")) {
//          children.hide('fast');
//          $(this).attr('title', 'Expand this branch').find(' > i').addClass('icon-plus-sign').removeClass('icon-minus-sign');
//      } else {
//          children.show('fast');
//          $(this).attr('title', 'Collapse this branch').find(' > i').addClass('icon-minus-sign').removeClass('icon-plus-sign');
//      }
//      e.stopPropagation();
//  });
//});

$(function(){
    $('.tree li:has(ul)').addClass('parent_li');
    $('.tree li.parent_li > span i').on('click', function (e) {
        var children = $(this).parents('li.parent_li').find(' > ul > li');
        if (children.is(":visible")) {
            children.hide('fast');
$(this).addClass('icon-plus-sign').removeClass('icon-minus-sign');
        } else {
            children.show('fast');
            $(this).addClass('icon-minus-sign').removeClass('icon-plus-sign');
        }
        e.stopPropagation();
    });
    //倒计时
    var offsetT=$(".add-mnew-countdown").offset().top*0.9;
    $(window).on('scroll', function() {
    	
    	var scrollT=$(document).scrollTop();
    	if(scrollT>offsetT){
    		$(".add-mnew-countdown").css({
    			position:"fixed",
    			background:"rgba(255,255,255,.1)",
    			top:"0",
    			border:"none",
    		})
    	}else{
    		$(".add-mnew-countdown").css({
    			position:"relative"
    			 }).css({
    			borderBottom:"1px solid #c3c3c3"
    			 })
    	}
    	
    	
    })
});