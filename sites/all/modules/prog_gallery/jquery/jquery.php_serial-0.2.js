/*
 * jQuery phpserial plugin
 *
 * Copyright (c) 2008 Power Howard Chen (powerhowardchen@gmail.com)
 *
 * Licensed under the GPL license:
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Version: 0.2
 */
jQuery.phpSerialize = function(data)
{
    function interSerialize(data)
    {
        var s = '';
        if(typeof(data) == 'object')
        {
            if(data instanceof Array)
            {
                var c = 0;
                for(var k in data)
                {
                    if((''+k).match(/^\-?\d+\.?\d*$/))  s += 'i:' + (k >= 0 ? Math.floor(k) : Math.ceil(k) )+ ';';
                    else                                s += 's:' + k.length + ':"' + k + '";';
                    s += interSerialize(data[k]);
                    c++;
                }
                s = 'a:' + c + ':{' + s + '}';
            }
            else
                s = 'N;';
        }
        else if(typeof(data) == 'boolean')
        {
            if (data)   s = 'b:1;';
            else        s = 'b:0;';
        }
        else
        {
            data = ''+data;
            if(data.match(/^[0-9]+$/))  s = 'i:' + data + ';';
            else                        s = 's:' + data.length + ':"' + data + '";';
        }
        return s;
    }
    return interSerialize(data);
};

jQuery.phpUnserialize = function(data)
{
    function interUnserialize()
    {
        if (data.charAt(1) != ':' || data.substr(0, 2) == 'N;')
        {
            data = (data.indexOf(';')>=0 ? data.substr(data.indexOf(';')+1) : '');
            return null;
        }
        switch (data.charAt(0))
        {
            case 'a':
                var c = data.replace( /^a\:(\d+)\:\{.+/ , '$1');
                var a = Array();
                data = data.substr(4+c.length);
                for (var i=0; i<c; i++)
                {
                    if (data.charAt(1) != ':')
                        return false;
                    switch (data.charAt(0))
                    {
                        case 'i':
                            var k = interUnserialize();
                            a[k] = interUnserialize();
                            break;
                        case 's':
                            var k = interUnserialize();
                            a[k] = interUnserialize();
                            break;
                        default:
                            return false;
                    }
                }
                if (data.charAt(0) != '}')
                    return false;
                data = data.substr(1);
                return a;
            case 'i':
            case 'd':
                var r = data.replace( /^[id]\:([^\;]+?)\;.*/ , '$1');
                data = data.substr(r.length+3);
                return 1*r;
            case 's':
                var l = data.replace( /^s\:(\d+)\:\".*\"\;.*/ , '$1');
                var r = data.substr(4+l.length, l);
                data = data.substr(6+l.length+(1*l));
                return r;
            case 'b':
                var r = data.substr(0, 4)
                data = data.substr(4);
                return r == 'b:1;';
        }
    }
    return interUnserialize();
};
