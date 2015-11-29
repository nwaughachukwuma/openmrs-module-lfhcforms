// The only way Ive found to disable/hide an exising widget that is not brought by an extension
// This will hide the immediate previous sibiling element with class="override-previous"

$(document).ready(function () {
	$(".override-previous").prev().css("display","none");
})