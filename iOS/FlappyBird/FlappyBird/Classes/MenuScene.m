//
//  MenuScene.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/7/14.
//  Copyright 2014 Hai Do Minh. All rights reserved.
//

#import "MenuScene.h"
#import "GameConfig.h"


@implementation MenuScene



+ (MenuScene *)scene
{
	return [[self alloc] init];
}

// -----------------------------------------------------------------------

- (id)init
{
    // Apple recommend assigning self with supers return value
    self = [super init];
    if (!self) return(nil);
    
    NSMutableDictionary* atlasInfo = GameConfig.atlasInfo;
    CCTexture *atlas = GameConfig.atlas;

    NSArray *bginfo = [atlasInfo objectForKey:@"bg_day"];
    CCSprite *background = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[bginfo objectAtIndex:3] intValue], [[bginfo objectAtIndex:4] intValue], [[bginfo objectAtIndex:1] intValue], [[bginfo objectAtIndex:2] intValue])];
    background.position = ccp(self.contentSize.width / 2, self.contentSize.height / 2);
    float scaleX = self.contentSize.width / background.contentSize.width;
    float scaleY = self.contentSize.height / background.contentSize.height;
    background.scaleX = scaleX;
    background.scaleY = scaleY;
    [self addChild:background];
    NSArray *footerinfo = [atlasInfo objectForKey:@"land"];
    CCSprite *footer = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue])];
    CCSprite *footer_1 = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[footerinfo objectAtIndex:3] intValue], [[footerinfo objectAtIndex:4] intValue], [[footerinfo objectAtIndex:1] intValue], [[footerinfo objectAtIndex:2] intValue])];
    footer.position = ccp(footer.contentSize.width / 2, footer.contentSize.height / 2);
    [self addChild:footer];
    footer_1.position = ccp(self.contentSize.width + footer.contentSize.width / 2, footer.position.y);
    [self addChild:footer_1];
    NSArray *copyinfo = [atlasInfo objectForKey:@"brand_copyright"];
    CCSprite *copy = [CCSprite spriteWithTexture:atlas rect:CGRectMake([[copyinfo objectAtIndex:3] intValue], [[copyinfo objectAtIndex:4] intValue], [[copyinfo objectAtIndex:1] intValue], [[copyinfo objectAtIndex:2] intValue])];
    copy.position = ccp(self.contentSize.width / 2, footer.contentSize.height / 2 );
    [self addChild:copy];
    // animate footer
    float footerV = 200.0;
    id footerAction = [CCActionRepeatForever actionWithAction: [CCActionSequence actionWithArray:@[[CCActionMoveTo actionWithDuration:(footer.contentSize.width * 2)/footerV position:CGPointMake(-footer.contentSize.width / 2, footer.position.y)], [CCActionMoveTo actionWithDuration:0.0 position:CGPointMake(footer_1.position.x + footer.contentSize.width / 2, footer.position.y)]]]];
    CCActionCallBlock *actionBlock = [CCActionCallBlock actionWithBlock:^{
        [footer_1 runAction:[CCActionSequence actionWithArray:@[[CCActionMoveTo actionWithDuration:0.5 position:CGPointMake(self.contentSize.width - footer.contentSize.width / 2, footer.position.y)], [CCActionMoveTo actionWithDuration:0.0 position:CGPointMake(self.contentSize.width + footer.contentSize.width / 2, footer.position.y)], actionBlock_1]]];
    }];
    CCActionCallBlock *actionBlock_1 = [CCActionCallBlock actionWithBlock:^{
        [footer runAction:footerAction];
    }];
    [footer runAction:[CCActionSequence actionWithArray:@[[CCActionMoveTo actionWithDuration:0.5 position:CGPointMake(self.contentSize.width - footer.contentSize.width / 2, footer.position.y)], [CCActionMoveTo actionWithDuration:0.0 position:CGPointMake(self.contentSize.width + footer.contentSize.width / 2, footer.position.y)], actionBlock]]];
//    [footer runAction:[CCActionSequence actionWithArray:@[[CCActionMoveTo actionWithDuration:(footer.contentSize.width - self.contentSize.width)/footerV position:CGPointMake(-footer.contentSize.width / 2, footer.position.y)],[CCActionMoveTo actionWithDuration:0.0 position:CGPointMake(footer.contentSize.width * 1.5, footer.position.y)], actionBlock]]];
//    [footer runAction:footerAction];
//    [footer_1 runAction:footerAction];
	
    // done
	return self;
}

@end
