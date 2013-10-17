/* 
	onload function
*/
jQuery(document).ready(function() {
	// load comments for this image node
	prog_gallery_ajaxcomments_getcomments(prog_gallery_nid);
	
	// modify "add comment" link-------
	$("li.comment_add > a").attr('href',	'#' + prog_gallery_nid);
	$("li.comment_add > a").click(function() {
			prog_gallery_ajaxcomments_form(prog_gallery_nid, "new",0,0);
			return false; 
	});
	// or we just remove "add comment" text from viewing the gallery
	//$("li.comment_add").html('');	
	
	// remove default comment add form (also clear comment form div)
	$("div.box").html('');
	
	// autostart lightbox2 gallery
	if (prog_gallery_lightbox2_autostart == 1) $(".gallery_album_entry :first").click();
});

