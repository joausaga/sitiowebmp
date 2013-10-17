<?php
/**
 * @file
 * Customize the e-mails sent by Webform after successful submission.
 *
 * This file may be renamed "webform-mail-[nid].tpl.php" to target a
 * specific webform e-mail on your site. Or you can leave it
 * "webform-mail.tpl.php" to affect all webform e-mails on your site.
 *
 * Available variables:
 * - $node: The node object for this webform.
 * - $submission: The webform submission.
 * - $email: The entire e-mail configuration settings.
 * - $user: The current user submitting the form.
 * - $ip_address: The IP address of the user submitting the form.
 *
 * The $email['email'] variable can be used to send different e-mails to different users
 * when using the "default" e-mail template.
 */
?>
<p>
Ha recibo una solicitud de <?php echo $email[subject] ?>, enviada el
<?php print t('!date', array('!date' => date('d/m/Y, H:i', $submission->submitted)));?>
</p>
<p>
<i><b><?php print t('Los datos enviados son') ?>:</b></i><br>
<?php print theme('webform_mail_fields', $submission, $node); ?>
</p>
<br>
<a href="http://www.movimientoperegrino.org/sw/sites/default/files/php_scripts/webform_results_from_email.php?nodeid=<?php echo $node->nid ?>">Descargar solicitudes en achivo</a>