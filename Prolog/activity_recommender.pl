% Define activities
activity(hiking).
activity(reading).
activity(watching_movie).
activity(cooking).
activity(playing_sports).

% Define preferences
preference(hiking, outdoor).
preference(reading, indoor).
preference(watching_movie, indoor).
preference(cooking, indoor).
preference(playing_sports, outdoor).

preference(hiking, active).
preference(reading, relaxing).
preference(watching_movie, relaxing).
preference(cooking, relaxing).
preference(playing_sports, active).

preference(hiking, solo).
preference(reading, solo).
preference(watching_movie, group).
preference(cooking, group).
preference(playing_sports, group).

% Define suggestion rule
suggest_activity(Activity, Environment, Energy, Group) :-
    activity(Activity),
    preference(Activity, Environment),
    preference(Activity, Energy),
    preference(Activity, Group).

% Interactive recommendation predicate
recommend_activity :-
    write('Do you prefer outdoor or indoor activities? '), nl,
    read(Environment),
    write('Do you want an active or relaxing activity? '), nl,
    read(Energy),
    write('Do you prefer solo or group activities? '), nl,
    read(Group),
    (   suggest_activity(Activity, Environment, Energy, Group)
    ->  format('Based on your preferences, you should try: ~w', [Activity]), nl
    ;   write('Sorry, no matching activity found for your preferences.'), nl
    ).
