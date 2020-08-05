function copyToClipboard(element) {
  /* Get the text field */
  var copyText = document.getElementById(element).href;

  /* Select the text field */
  copyText.select(); 
  copyText.setSelectionRange(0, 99999); /*For mobile devices*/

  /* Copy the text inside the text field */
  document.execCommand("copy");

  /* Alert the copied text */
  alert("Abo-Link kopiert: " + copyText.value);
}

function showComment(element) {
    element.classList.add("d-none");
    document.getElementById("comment").classList.remove("d-none");
}