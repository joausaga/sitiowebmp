  <div class="node<?php if ($sticky) { print " sticky"; } ?><?php if (!$status) { print " node-unpublished"; } ?>">
    <?php if ($picture) {
      print $picture;
    }?>
    <?php if ($page == 0) { ?><h2 class="title"><a href="<?php print $node_url?>"><?php print $title?></a></h2><?php }; ?>
  
   
    <div class="content"><?php print $content?></div>
	  <div class="postmeta"><span class="submitted"><?php print $submitted?></span> <span class="taxonomy"><?php print $terms?></span></div>
  </div>
