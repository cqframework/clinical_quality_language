drop function dbo.CompareIntegerUncertainty;
create function dbo.CompareIntegerUncertainty(@LeftLow integer, @LeftHigh integer, @RightLow integer, @RightHigh integer) 
	returns integer
as
begin
	return
		case
			// not(Left >= Right)
			when @LeftLow = @LeftHigh and @LeftLow = @RightLow and @LeftHigh = @RightHigh then 0
			when @LeftHigh < @RightLow then -1
			when @LeftLow > @RightHigh then 1
			else null
		end
end
go

create table Uncertainty 
( 
	ID integer identity(1, 1) not null, 
	Low integer not null, 
	High integer not null, 
	constraint PK_Uncertainty primary key ( ID ),
	constraint CK_UncertaintyValid check (Low <= High)
);

insert into Uncertainty ( Low, High ) values ( 1, 1 )
insert into Uncertainty ( Low, High ) values ( 3, 3 )
insert into Uncertainty ( Low, High ) values ( 5, 5 )

insert into Uncertainty ( Low, High ) values ( 1, 3 )
insert into Uncertainty ( Low, High ) values ( 2, 4 )
insert into Uncertainty ( Low, High ) values ( 3, 5 )

insert into Uncertainty ( Low, High ) values ( 1, 2 )
insert into Uncertainty ( Low, High ) values ( 2, 3 )
insert into Uncertainty ( Low, High ) values ( 3, 4 )

select A.Low, A.High, B.Low, B.High, dbo.CompareIntegerUncertainty(A.Low, A.High, B.Low, B.High) ComparisonResult
	from Uncertainty A cross join uncertainty B

