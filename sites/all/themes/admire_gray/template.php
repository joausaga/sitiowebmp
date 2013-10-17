<?php 
function phptemplate_body_class($left, $right) {
  if ($left != '' && $right != '') {
    $class = 'sidebars';
  }
  else {
    if ($left != '') {
      $class = 'sidebar-left';
    }
    if ($right != '') {
      $class = 'sidebar-right';
    }
  }

  if (isset($class)) {
    print ' class="'. $class .'"';
  }
}
function admire_gray_theme(&$existing, $type, $theme, $path) {
  $hooks['comment_form'] = array('arguments' => array('form' => NULL));
  return $hooks;// I've named the function according to theming standards, replacing "theme" with our theme's name
}
function admire_gray_preprocess_node(&$vars) 
{
   $vars['content_bottom'] = theme('blocks', 'content_bottom');
}
//Funcion que elimina el comentario (no verificado) colocado a lado de los nombre de las personas anonimas que envian sus comentarios
function admire_gray_username($object) {

  if ($object->uid && $object->name) {
    if (drupal_strlen($object->name) > 20) {
      $name = drupal_substr($object->name, 0, 15) .'...';
    }
    else {
      $name = $object->name;
    }

    if (user_access('access user profiles')) {
      $output = l($name, 'user/'. $object->uid, array('attributes' => array('title' => t('View user profile.'))));
    }
    else {
      $output = check_plain($name);
    }
  }
  else if ($object->name) {
    if (!empty($object->homepage)) {
      $output = l($object->name, $object->homepage, array('attributes' => array('rel' => 'nofollow')));
    }
    else {
      $output = check_plain($object->name);
    }

    /**
     * HERE I've commented out the next line, which is the line that was adding
     * the unwanted text to our author names!
     */ 
    // $output .= ' ('. t('not verified') .')';
  }
  else {
    $output = variable_get('anonymous', t('Anonymous'));
  }

  return $output;
}
//Funcion que quita el campo Home Page del formulario de envio de comentarios
function admire_gray_comment_form($form) {
  $output = '';
  unset ($form['homepage']);
  $output .= drupal_render($form);
  return $output;
}
//Funcion que quita los links responder de los comentarios
function phptemplate_links($links, $attributes = array('class' => 'links')) {
  //unset($links['comment_reply']);
  return theme_links($links, $attributes);
}

/*!
 * Dynamic display block preprocess functions
 * Copyright (c) 2008 - 2009 P. Blaauw All rights reserved.
 * Version 1.4 (01-SEP-2009)
 * Licenced under GPL license
 * http://www.gnu.org/licenses/gpl.html
 */

 /**
 * Override or insert variables into the ddblock_cycle_block_content templates.
 *   Used to convert variables from view_fields to slider_items template variables
 *
 * @param $vars
 *   An array of variables to pass to the theme template.
 * 
 */
function admire_gray_preprocess_ddblock_cycle_block_content(&$vars) {
  if ($vars['output_type'] == 'view_fields') {
    $content = array();
    // Add slider_items for the template 
    // If you use the devel module uncomment the following line to see the theme variables
    //dsm($vars['settings']['view_name']);  
    //dsm($vars['content'][0]);
    // If you don't use the devel module uncomment the following line to see the theme variables
    //drupal_set_message('<pre>' . var_export($vars['settings']['view_name'], true) . '</pre>');
    //drupal_set_message('<pre>' . var_export($vars['content'][0], true) . '</pre>');
    if ($vars['settings']['view_name'] == 'news_items') {
      if (!empty($vars['content'])) {
        foreach ($vars['content'] as $key1 => $result) {
          // add slide_image variable 
          if (isset($result->node_data_field_image_field_image_fid)) {
            // get image id
            $fid = $result->node_data_field_image_field_image_fid;
            // get path to image
            $filepath = db_result(db_query("SELECT filepath FROM {files} WHERE fid = %d", $fid));
            //  use imagecache (imagecache, preset_name, file_path, alt, title, array of attributes)
            if (module_exists('imagecache') && is_array(imagecache_presets()) && $vars['imgcache_slide'] <> '<none>'){
              $slider_items[$key1]['slide_image'] = 
              theme('imagecache', 
                    $vars['imgcache_slide'], 
                    $filepath,
                    $result->node_title);
            }
            else {          
              $slider_items[$key1]['slide_image'] = 
                '<img src="' . base_path() . $filepath . 
                '" alt="' . $result->node_title . 
                '"/>';     
            }          
          }
          // add slide_text variable
          if (isset($result->node_data_field_pager_item_text_field_slide_text_value)) {
            $slider_items[$key1]['slide_text'] =  $result->node_data_field_pager_item_text_field_slide_text_value;
          }
          // add slide_title variable
          if (isset($result->node_title)) {
            $slider_items[$key1]['slide_title'] =  $result->node_title;
          }
          // add slide_read_more variable and slide_node variable
	  if (isset($result->node_data_field_pager_item_text_field_url_nid)) {
	    $slider_items[$key1]['slide_read_more'] =  l('Leer mas...', 'node/' . $result->node_data_field_pager_item_text_field_url_nid);
	  }
	  elseif (isset($result->nid)) {
  	    $slider_items[$key1]['slide_read_more'] =  l('Leer mas...', 'node/' . $result->nid);
  	    $slider_items[$key1]['slide_node'] =  'node/' . $result->nid;
	  }
        }
        $vars['slider_items'] = $slider_items;
      }
    }    
  }
}  
/**
 * Override or insert variables into the ddblock_cycle_pager_content templates.
 *   Used to convert variables from view_fields  to pager_items template variables
 *  Only used for custom pager items
 *
 * @param $vars
 *   An array of variables to pass to the theme template.
 *
 */
function admire_gray_preprocess_ddblock_cycle_pager_content(&$vars) {
  if (($vars['output_type'] == 'view_fields') && ($vars['pager_settings']['pager'] == 'custom-pager')){
    $content = array();
    // Add pager_items for the template 
    // If you use the devel module uncomment the following lines to see the theme variables
    //dsm($vars['pager_settings']['view_name']);     
    //dsm($vars['content'][0]);     
    // If you don't use the devel module uncomment the following lines to see the theme variables
    //drupal_set_message('<pre>' . var_export($vars['pager_settings'], true) . '</pre>');
    //drupal_set_message('<pre>' . var_export($vars['content'][0], true) . '</pre>');
    if ($vars['pager_settings']['view_name'] == 'news_items') {
      if (!empty($vars['content'])) {
        foreach ($vars['content'] as $key1 => $result) {
          // add pager_item_image variable
          if (isset($result->node_data_field_pager_item_text_field_image_fid)) {
            $fid = $result->node_data_field_pager_item_text_field_image_fid;
            $filepath = db_result(db_query("SELECT filepath FROM {files} WHERE fid = %d", $fid));
            //  use imagecache (imagecache, preset_name, file_path, alt, title, array of attributes)
            if (module_exists('imagecache') && 
                is_array(imagecache_presets()) && 
                $vars['imgcache_pager_item'] <> '<none>'){
              $pager_items[$key1]['image'] = 
                theme('imagecache', 
                      $vars['pager_settings']['imgcache_pager_item'],              
                      $filepath,
                      $result->node_data_field_pager_item_text_field_pager_item_text_value);
            }
            else {          
              $pager_items[$key1]['image'] = 
                '<img src="' . base_path() . $filepath . 
                '" alt="' . $result->node_data_field_pager_item_text_field_pager_item_text_value . 
                '"/>';     
            }          
          }
          // add pager_item _text variable
          if (isset($result->node_data_field_pager_item_text_field_pager_item_text_value)) {
            $pager_items[$key1]['text'] =  $result->node_data_field_pager_item_text_field_pager_item_text_value;
          }
        }
      }
      $vars['pager_items'] = $pager_items;
    }
  }    
}

function admire_gray_webform_mail_headers($node, $submission, $email) {
  $headers = array(
    'Content-Type'  => 'text/html; charset=UTF-8; format=flowed; delsp=yes',
    'X-Mailer' => 'Drupal Webform (PHP/' . phpversion() . ')',
  );
  return $headers;
}

function admire_gray_webform_mail_fields($submission,$node) {
    if($submission){
        foreach($node->webform['components'] as $eid => $entry) {
            if (!in_array($entry['type'], array('markup', 'pagebreak', 'hidden'))) {
                if($entry['type']=='fieldset'){               
                    print('<br><u>'.$entry['name'].'</u>');
                }
                elseif($entry['type']=='select') {
                    if ($entry['extra']['items']) {
                        //create an array from the string of all possible values
                        $list = explode("\n",$entry['extra']['items']);
                        //cycle the array
                        foreach($list as $list_item) {
                            //find the pipe and the key/value on either side of it                            
                            $pos = strpos($list_item,'|');
                            $key = substr($list_item,0,$pos);
                            $value = substr($list_item,($pos + 1));
                            //if there is a key match, write the value
                            if(trim($key) != '' and trim($key) == $submission->data[$eid]['value']['0']) {
                                print('<br><b>'.$entry['name'].':</b> ');
                                print nl2br(trim($value));
                                break;
                            }
                        }
                    }
                }
                else {
                    if($submission->data[$eid]['value'][0]) {
                        if($entry['type']=='textarea')                        
                            print('<br><b>'.$entry['name'].':</b><br>');
                        else
                            print('<br><b>'.$entry['name'].':</b> ');
                        foreach($submission->data[$eid]['value'] as $vid => $value)
                            print nl2br($value); 
                    }
                }
            }
        }
    }
}
