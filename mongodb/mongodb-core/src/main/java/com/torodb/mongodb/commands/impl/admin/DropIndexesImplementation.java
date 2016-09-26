/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.mongodb.commands.impl.admin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.eightkdata.mongowp.Status;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.admin.DropIndexesCommand.DropIndexesArgument;
import com.eightkdata.mongowp.server.api.Command;
import com.eightkdata.mongowp.server.api.Request;
import com.eightkdata.mongowp.server.api.tools.Empty;
import com.torodb.mongodb.commands.impl.WriteTorodbCommandImpl;
import com.torodb.mongodb.core.WriteMongodTransaction;
import com.torodb.mongodb.language.Constants;

public class DropIndexesImplementation implements WriteTorodbCommandImpl<DropIndexesArgument, Empty> {

    @Override
    public Status<Empty> apply(Request req, Command<? super DropIndexesArgument, ? super Empty> command,
            DropIndexesArgument arg, WriteMongodTransaction context) {
        List<String> indexesToDrop;
        
        if (!arg.isDropAllIndexes()) {
            indexesToDrop = Arrays.asList(arg.getIndexToDrop());
        } else {
            indexesToDrop = context.getTorodTransaction().getIndexesInfo(req.getDatabase(), arg.getCollection())
                .filter(indexInfo -> indexInfo.getName().equals(Constants.ID_INDEX))
                .map(indexInfo -> indexInfo.getName())
                .collect(Collectors.toList());   
        }
        
        for (String indexToDrop : indexesToDrop) {
            context.getTorodTransaction().dropIndex(req.getDatabase(), arg.getCollection(), indexToDrop);
        }

        return Status.ok();
    }

}
