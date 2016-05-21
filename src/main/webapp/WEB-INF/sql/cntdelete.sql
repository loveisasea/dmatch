select 'select count(*) as cnt,''' || tablename || ''' as tablename,''delete from ' || tablename || ';'' as delexe from ' || tablename || ' ' as sen from pg_tables where tableowner = 'dchess';

