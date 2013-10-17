// AJAX`IZING THE COMMENTS

/* 
	display comment form
*/
function prog_gallery_ajaxcomments_form(nid,action,pid,cid) {
	if (pid) pid = pid; else pid = 0;
	
	var post_arr = new Array();
	var post_arr_index = 0;
	
	// add hidden attributes to post array
	if ((action != "new") && (action != "edit")) {
		$('#comment-form textarea, #comment-form input:not(input:radio,input:button), #comment-form input:radio:selected').each( function (i) {
			post_arr[post_arr_index] = new Array();
			post_arr[post_arr_index]["name"] = $(this).attr("name");
			post_arr[post_arr_index]["value"] = $(this).val();
			post_arr_index++;
		});
	}
	
	if (pid) {
		post_arr[post_arr_index] = new Array();
		post_arr[post_arr_index]["name"] = "pid";
		post_arr[post_arr_index]["value"] = pid;
		post_arr_index++;
	}
	
	if (cid) {
		post_arr[post_arr_index] = new Array();
		post_arr[post_arr_index]["name"] = "cid";
		post_arr[post_arr_index]["value"] = cid;
		post_arr_index++;
	}

	post_arr[post_arr_index] = new Array();
	post_arr[post_arr_index]["name"] = "op";
	post_arr[post_arr_index]["value"] = action;
	post_arr_index++;
	
	$.post(base_path + 'node/' + nid + '/ajax/get_comments_form', post_arr ,
  		function(data){
				if (data == 'refresh_comments') {
					prog_gallery_ajaxcomments_getcomments(nid);
					$("div.box").html("");
				} else {
					// display output
		  		$("div.box").html(data);
		  			
		  			// set action for buttons
					$('#comment-form :button').each( function (i) {
						$(this).click(function(){
							prog_gallery_ajaxcomments_form(nid,$(this).val(),pid,cid);
						});
					});
		  			
					// remove form submit action
					$("#comment-form").submit(function(){return false;});
					
					// re-run onLoad scripts using drupal javascript behaviours
					Drupal.attachBehaviors($("div.box"));
					
				}
			}
	);
	$("div.box").html('<img src="' + prog_gallery_base + 'images/loading.gif" alt="loading"/>');
	
	return false;
}

/*
	Get comments for specified node, 
	replace delete/edit/reply links with javascript wrappers
*/

function prog_gallery_ajaxcomments_getcomments(nid) {
	$("#comments").html('<img src="' + prog_gallery_base + 'images/loading.gif" alt="loading"/>');
	$("#comments").load(base_path + 'node/' + nid + '/ajax/get_comments',{},function(){
		// add div.box container since d6 doesnt include that one
		$("#comments").prepend('<div class="box"></div>');
		
    //rewrite delete link------
		$('li.comment_delete > a').click(function() {
			var cid = $(this).attr('href').split('/');
			cid = cid[cid.length-1];
			if (confirm("Delete comment?")){
				$("#comments").html('<img src="' + prog_gallery_base + 'images/loading.gif" alt="loading"/>');
				$.post(base_path + 'node/' + nid + '/ajax/delete_comment', { cid: cid },
					function(data){
						if (data == 'refresh_comments') {
							prog_gallery_ajaxcomments_getcomments(nid);
					}
				});
			}
			return false; 
		});
		
		// rewrite edit link------
		$('li.comment_edit > a').click(function() {
			var cid = $(this).attr('href').split('/');
			prog_gallery_ajaxcomments_form(nid,"edit",0,cid[cid.length-1])
			return false; 
		});
		
		// rewrite reply link------
		$('li.comment_reply > a').click(function() {
			var pid = $(this).attr('href').split('/');
			prog_gallery_ajaxcomments_form(nid, "new", pid[pid.length-1],0);
			return false; 
		});
		
		// re-run onLoad scripts using drupal javascript behaviours
		Drupal.attachBehaviors($("#comments"));
		
	});
}

