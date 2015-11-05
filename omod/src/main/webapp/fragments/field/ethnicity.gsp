<!-- <p class="required"> -->
<p>
    <label>
        ${config.label}
        <!-- <span>(${ ui.message("emr.formValidation.messages.requiredField.label") })</span> -->
    </label>
    <select name="${config.formFieldName}" size="6">
        <option value ="LFHC:901">Lao</option>
        <option value ="LFHC:902">Hmong</option>
        <option value ="LFHC:905">Khmuic</option>
        <option value ="LFHC:906">Other Lao</option>
        <option value ="LFHC:911">Foreigner</option>
    </select>
    <span class="field-error"></span>
</p>