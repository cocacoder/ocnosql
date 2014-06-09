list
disable 'auto_func_test'
drop 'auto_func_test'
list
create 'auto_func_test', 'f', {SPLITS => ['10','20']}
list
describe 'auto_func_test'
for i in 10..20 do for j in 'a'..'z' do put 'auto_func_test',"#{i}","f:c#{j}","value#{j}" end end
flush 'auto_func_test'
list
scan 'auto_func_test', {STARTROW => '10', ENDROW=>'20'}
split 'auto_func_test','60'
scan '.META.', {COLUMNS => 'info:regioninfo',FILTER => "(PrefixFilter ('auto_func_test')"}
for i in 50..60 do for j in 'a'..'z' do put 'auto_func_test',"#{i}","f:c#{j}","value#{j}" end end
list
flush 'auto_func_test'
count 'auto_func_test'
list
exit
