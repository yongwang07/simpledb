const readline = require('readline'), colorText = text => `\x1b[31m${text}`;

const trans = [], db = {}, nums = new Map();

const setValue = (arg, value) => {
    let old = db[arg];
    if (!!old) {
        nums.set(old, nums.get(old) - 1);
        if (nums.get(old) === 0) nums.delete(old);
    }
    if (!!value) nums.set(value, (nums.get(value) || 0) + 1);

    if (!value) delete db[arg];
    else db[arg] = value;
};

const getValue = arg => db[arg];

const unSet = arg => db[arg] && setValue(arg);

const rollback = () => {
    if (trans.length === 0) throw new Error('NO TRANSACTION');
    for (let cb of trans.pop()) {
        cb();
    }
};

const commit = () => {
    if (trans.length === 0) throw new Error('NO TRANSACTION');
    trans.length = 0;
};

const addReverseCommand = (cb, variable, value) => {
    if (trans.length) {
        const tran = trans[trans.length - 1];
        tran.push(cb.bind(null, variable, value));
    }
}

const commands = {
    set: args => {
        if (args.length < 2 || isNaN(+args[1])) throw new Error(colorText('set command invalid(set variable value)'));
        addReverseCommand((variable, value) => setValue(variable, value), args[0], db[args[0]]);
        setValue(args[0], +args[1]);
    },

    get: args => {
        if (args.length == 0) throw new Error(colorText('get command invalid(get variable)'));
        console.log(colorText(getValue(args[0]) || 'NULL'));
    },

    unset: args => {
        if (args.length == 0) throw new Error(colorText('unset command invalid(unset variable)'));
        addReverseCommand((varialbe, value) => setValue(varialbe, value), args[0], db[args[0]]);
        unSet(args[0]);
    },

    numequalto: args => {
        if (args.length < 1 || isNaN(+args[0])) throw new Error(colorText('equal command invalid(numequalto number)'));
        console.log(colorText(nums.get(+args[0]) || 0));
    },

    begin: () => trans.push([]),

    rollback: () => rollback(),
    
    commit: () => commit(),
    
    end: () => {
        r1.close();
        process.exit(0);
    }
};
const r1 = readline.createInterface({input:process.stdin, output:process.stdout, terminal:false});
r1.setPrompt(`\x1B[39m>`);
r1.prompt();
r1.on('line', function(cmd) {
    const command = cmd.trim().split(/\s+/), commandName = command[0].toLowerCase();
    if (commands[commandName]) {
        try {
            commands[commandName].call(null, command.splice(1));
        } catch(e) {
            console.error(e.message);
        }
    } else console.log(colorText('unknow command'));
    r1.prompt();
});