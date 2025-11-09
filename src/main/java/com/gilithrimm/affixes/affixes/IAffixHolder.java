package com.gilithrimm.affixes.affixes;

import java.util.List;

public interface IAffixHolder {
   boolean addAffix(Affix affix);

   List<Affix> getAffixes();

   boolean removeAffix(Affix affix);

   boolean containsAffix(Affix affix);
}
