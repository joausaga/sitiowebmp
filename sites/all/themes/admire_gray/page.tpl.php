<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <title><?php print $head_title ?></title>
    <?php print $head ?>
    <?php print $styles ?>
    <?php print $scripts ?>
    <!--[if lt IE 7]>
    <style type="text/css" media="all">@import "<?php print base_path() . path_to_theme() ?>/fix-ie.css";</style>
    <![endif]-->
    <script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-36308208-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
  </head>
<body <?php print phptemplate_body_class($left, $right); ?>>
<div id="container">
<div id="container2">

<div id="header">

<div id="blogdesc">
 <?php if ($logo) { ?><div id="logocontainer"><a href="<?php print $base_path ?>" title="<?php print t('Home') ?>"><img src="<?php print $logo ?>" alt="<?php print t('Home') ?>" /></a></div><?php } ?>
      <div id="texttitles">
	  <?php if ($site_name) { ?><h1 class='site-name'><a href="<?php print $base_path ?>" title="<?php print t('Home') ?>"><?php print $site_name ?></a></h1><?php } ?>
      <?php if ($site_slogan) { ?><div class='site-slogan'><?php print $site_slogan ?></div><?php } ?>
</div></div>

<?php if ($header): ?>

<div id="header-content">
<?php print $header ?>
</div>
<?php endif; ?>

</div>

<div id="navigation">
      <?php if (isset($primary_links)) { ?><?php print theme('links', $primary_links, array('class' =>'primary_nav1')) ?><?php } ?>
</div>
 <?php if (isset($secondary_links)) { ?>
<div id="navigation2">
<?php print theme('links', $secondary_links, array('class' => 'links', 'id' => 'subnavlist')) ?></div>
<?php } ?>

<div id="undernavigation">
</div>

<div id="wrap">
<?php if ($left): ?>
<div id="leftside">
<?php print $left ?>
</div>
<?php endif; ?>

<div id="contentmiddle">

<?php if ($content_top): ?>

<div id="content_top">
<?php print $content_top ?>
</div>

<?php endif; ?>

<?php if ($mission) { ?><div id="mission"><div class="inner"> <div id="missiontitle"><h3>Our Mission</h3></div><?php print $mission ?></div></div><?php } ?>



          <?php if ($breadcrumb): print $breadcrumb; endif; ?>


          <?php if ($tabs): print '<div id="tabs-wrapper" class="clear-block">'; endif; ?>
          <div id="h2title"><?php if ($title): print '<h2'. ($tabs ? ' class="with-tabs"' : '') .'>'. $title .'</h2>'; endif; ?>
		  </div>
          <?php if ($tabs): print $tabs .'</div>'; endif; ?>

          <?php if (isset($tabs2)): print $tabs2; endif; ?>
          <?php if ($help): print $help; endif; ?>
          <?php if ($messages): print $messages; endif; ?>
          <?php print $content ?>
          <?php print $node->content['fb_social_comments_facebook_comments']['#value']; ?>
          <?php print $content_bottom ?>
          <?php print $comments ?>
</div>

<?php if ($rightup): ?>
<div id="rightupside">
<?php print $rightup ?>
</div>

<?php endif; ?>

<?php if ($right): ?>
<div id="rightside">
<?php print $right ?>
</div>
<?php endif; ?>



</div>
</div>
</div>
<div id="footer">
<?php if ($footer): ?>
<?php print $footer ?>
<?php endif; ?>
  <?php print $footer_message ?><br />
<?php ?>
<?php print $closure ?>
<!--Designer Worthapost-->
</body>
</html>