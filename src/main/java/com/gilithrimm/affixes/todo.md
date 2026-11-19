1. add docs where missing
2. unit tests
    1. figure out what even *can* get unit tested
    2. EXTRACT THE LOGIC OUT OF MIXINS!!!!!
3. affix costs/slots/whatever the fuck we call it
    1. add to [Affix.java](affixes/Affix.java) (as required?)
    2. add to armor/tools as a stat via mixins
        1. unique tools (like bow/crossbow/turtle helmet)?
    3. add checks to the recipe to prevent going over
    4. should we keep [/affix](command/AffixCommand.java) unlimited??
    5. can translate into weights for scribed affix??
    6. matcha support after port?
    7. treasure enchantment to add extra slots
4. figure out a good way to test affixes
    1. affixes are stored as named consts after getting registered
    2. ∴ we can make a unit test for each affix
    3. provide context via mocks & stuff
5. splitting mixins
    1. mainly based on what's being mixed & why
6. splitting the entire project into `lib` and `mod`
    1. so-called Hook API goes in full to `lib`
    2. premade affixes -> `mod`
    3. status effects -> `mod`
    4. config gets split
    5. items -> `lib`???
    6. mixins -> split
    7. charge & hunter's mark -> `mod`
    8. registry -> `lib`
    9. builders -> `mod` probs
    10. /affix command -> `lib`
    11. textures -> `mod`
    12. lang -> split
    13. datapack -> split??
7. ci/cd
8. extra data for affix to store on itself
    1. map<string, T> similar to fxgl??
    2. basically better way to store data between triggers
    3. hopefully fixes the syncing problem??
9. hook priority???
    1. guarantee certain hooks happen first/last/??
    2. resolving same priority??
10. projectile based weapon support (mainly bow & crossbow)
11. non-combat affixes support??
12. figure out proper placement & order of this mod in the game progression
    1. placement - probs mid to late game??
    2. order: baseline steps from 0 to mod completion
13. rebalance bleed till it feels good enough
14. puncturing wounds full rework
    1. no more infinity stacking lvls!
15. most bleed affixes check & rework
16. hunter's mark full rework
    1. use custom status effects??
    2. extra dmg from ALL sources (const Fragile 5??)
    3. exclude MmmMmmMmmMmm's dummies (can't be killed)
    4. time limit on the mark?? to give target potential breathing room?
    5. add visuals for hunter (highlight marked entities?) & marked (status effect?)
17. charge full rework
    1. limit charge to 0..100 (*)
    2. overcharge as a potential mechanic (with consequences!)
    3. add visuals (item to store charge??)
18. add lang where missing
19. alternative ways of getting affixes
    1. structure loot
    2. archaeology
    3. trading??? probs requires a new block as station...
20. a way to change / remove affixes
    1. affix reroll??
        1. maybe only for stored affixes
    2. change the way we get stored affixes items
21. patchouli support
    1. tells order (!)
    2. detailed desc of each affix (pull from docs??)
22. jei/rei/emi support???
    1. recipes get auto'd anyhow
    2. [jeed](https://modrinth.com/mod/just-enough-effect-descriptions-jeed) support
    3. loot tables are auto'd (i think??)
23. work on no-op affixes cuz they're polluting the namespace rn
24. at least 2-3 affixes for each status effect?? for a more 'proper' alchemist build lmao
25. textures!!
26. switch mapping to mojang
27. port to later versions
