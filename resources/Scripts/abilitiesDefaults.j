native LoadScriptFile takes string path, string mainFunction returns boolean
native GetEnumFilePath takes nothing returns string
native ForFiles takes string directoryPath, code func returns nothing

function AbilitiesFileEnum takes nothing returns nothing
    call LoadScriptFile(GetEnumFilePath(), "main")
    call ExecuteFunc("init")
endfunction

function abilities_main takes nothing returns nothing
    call LoadScriptFile("Scripts\\abilitiesUtils.j", null)
    call ForFiles("Scripts\\Abilities\\", function AbilitiesFileEnum)
endfunction
