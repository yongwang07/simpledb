const readline = require('readline');
const colorText = (text) => '\x1b[31m' + text;
const trans = [], db = {}, eqVals = {};
const changeEqVals = (old, cur) => {
    if (old !== null) {
        eqVals[old.toString()] = eqVals[old.toString()] - 1;
        if (eqVals[old.toString()] === 0) delete eqVals[old.toString()];
    }
    if (cur !== null) eqVals[cur.toString()] = (eqVals[cur.toString()] || 0) + 1;
};
const setValue = (arg, value) => {
    if (!db[arg]) db[arg] = [];
    const valueSet = db[arg];
    let old = valueSet.length > 0 ? valueSet[valueSet.length - 1] : null;
    if (valueSet.length === 0 || 
        trans.length && trans[trans.length - 1][arg] === undefined) {
        db[arg].push(value);
    }
    else {
        valueSet[valueSet.length - 1] = value;
    }
    if (trans.length > 0) trans[trans.length - 1][arg] = true;
    changeEqVals(old, value);
};
const getValue = (arg) => db[arg] && db[arg][db[arg].length - 1];
const unSet = (arg) => db[arg] && setValue(arg, null);
const rollback = () => {
    if (trans.length === 0) throw new Error('NO TRANSACTION');
    let args = trans.pop();
    Object.keys(args).forEach(arg => {
        let valueSet = db[arg];
        let old =  valueSet.pop();
        let cur = valueSet.length && valueSet[valueSet.length - 1];
        changeEqVals(old, cur);  
    });
};
const commit = () => {
    if (trans.length === 0) throw new Error('NO TRANSACTION');
    let args = trans.pop();
    Object.keys(args).forEach(arg => {
        let valueSet = db[arg];
        let old = valueSet.pop();
        if (valueSet.length > 0) db[arg][db[arg].length - 1] = old;
        else db[arg] = [old];
    });
};
const commands = {
    set: (args) => {
        if (args.length < 2 || 
            isNaN(+args[1])) throw new Error(colorText('set command wrong'));
            setValue(args[0], args[1]);
        },
    get: (args) => {
        if (args.length == 0) throw new Error(colorText('get command wrong'));
        console.log(colorText(getValue(args[0]) || 'NULL'));
    },
    unset: (args) => {
        if (args.length == 0) throw new Error(colorText('unset command wrong'));
        unSet(args[0]);
    },
    numequalto: (args) => {
        if (args.length < 1 || 
            isNaN(+args[0])) throw new Error(colorText('equal command wrong'));
        console.log(colorText(eqVals[args[0].toString()] || 0));
    },
    begin: () => trans.push({}),
    rollback: () => rollback(),
    commit: () => commit(),
    end: () => {
        r1.close();
        process.exit(0);
    }
};
const r1 = readline.createInterface(process.stdin, process.stdout);
r1.setPrompt('\x1B[39m' + '> ');
r1.prompt();
r1.on('line', function(cmd) {
    let command = cmd.trim().split(/\s+/);
    let commandName = command[0].toLowerCase();
    if (commands[commandName]) {
        try {
            commands[commandName].call(null, command.splice(1));
        } catch(e) {console.log(e.message);}
    }
    r1.prompt();
});